package com.fourthwall.googlemembersapi.infrastructure

import java.time.Instant

import cats.effect.IO
import cats.instances.option._
import cats.instances.try_._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.traverse._
import com.fourthwall.googlemembersapi.api.{
  Forbidden,
  InvalidArgument,
  NotFound,
  ServiceUnavailable,
  Unauthorized,
  ValidationFailureException
}
import com.typesafe.scalalogging.StrictLogging
import io.circe.Encoder
import io.circe.syntax._
import org.springframework.http.{HttpMethod, HttpStatus, MediaType}
import org.springframework.web.reactive.function.server
import org.springframework.web.reactive.function.server.RouterFunctions.{route, Builder}
import org.springframework.web.reactive.function.server.{HandlerFunction, RouterFunction, ServerRequest, ServerResponse}
import reactor.core.publisher.Mono

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

trait WebFluxUtils extends StrictLogging {

  val EmptyResponse: Option[Nothing] = None

  //noinspection ReactorUnusedPublisher
  def toMono[T](io: IO[T]): Mono[T] = Mono.create { sink =>
    def callback(result: Either[Throwable, T]): Unit = result match {
      case Left(value) => sink.error(value)
      case Right(value) => sink.success(value)
    }
    io.unsafeRunAsync(callback)
  }

  def toInstant(s: String): Try[Instant] =
    Try(Instant.ofEpochSecond(s.toLong))
      .orElse(Try(Instant.parse(s)))

  def endpoint[T](
    method: HttpMethod,
    path: String,
    successCode: HttpStatus = HttpStatus.OK,
    handler: (server.ServerRequest, HttpStatus) => Mono[ServerResponse]
  ): RouterFunction[ServerResponse] = {
    val methodBuilder: (String, HandlerFunction[ServerResponse]) => Builder =
      method match {
        case HttpMethod.GET => route().GET
        case HttpMethod.HEAD => route().HEAD
        case HttpMethod.POST => route().POST
        case HttpMethod.PUT => route().PUT
        case HttpMethod.PATCH => route().PATCH
        case HttpMethod.DELETE => route().DELETE
        case HttpMethod.OPTIONS => route().OPTIONS
        case HttpMethod.TRACE => throw new RuntimeException("TRACE http method is not supported by spring webflux")
      }

    methodBuilder(path, (request: ServerRequest) => handler(request, successCode)).build()
  }

  def withoutRequestBody(
    handler: (Map[String, String], Map[String, String]) => IO[Option[String]]
  )(request: server.ServerRequest, successCode: HttpStatus): Mono[ServerResponse] = {
    val result = handler(queryParams(request), pathVariables(request))
    handleResult(result, successCode)
  }

  //noinspection ReactorUnusedPublisher
  def withRequestBody[T](
    bodyParser: String => Either[Throwable, T]
  )(
    handler: (T, Map[String, String], Map[String, String]) => IO[Option[String]]
  )(request: server.ServerRequest, successCode: HttpStatus): Mono[ServerResponse] =
    request
      .bodyToMono(classOf[String])
      .flatMap(stringBody => {
        val body = bodyParser(stringBody).toTry.get
        val result = handler(body, queryParams(request), pathVariables(request))

        handleResult(result, successCode)
      })

  private def pathVariables[T](request: ServerRequest) = {
    request.pathVariables().asScala.toMap
  }

  private def queryParams[T](request: ServerRequest) = {
    request.queryParams().asScala.mapValues(_.asScala.head).toMap
  }

  //noinspection ReactorUnusedPublisher
  private def handleResult[T](result: IO[Option[String]], successCode: HttpStatus): Mono[ServerResponse] = {
    val monoResult = toMono(result)
    monoResult.flatMap {
      case Some(value) =>
        ServerResponse
          .status(successCode)
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .body(Mono.just(value), classOf[String])
      case None =>
        ServerResponse.status(successCode).build()
    }.onErrorResume {
      case _: ServiceUnavailable =>
        ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build()
      case _: NotFound =>
        ServerResponse.status(HttpStatus.NOT_FOUND).build()
      case _: InvalidArgument =>
        ServerResponse.status(HttpStatus.BAD_REQUEST).build()
      case _: Unauthorized =>
        ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
      case _: Forbidden =>
        ServerResponse.status(HttpStatus.FORBIDDEN).build()
      case _: ValidationFailureException =>
        ServerResponse.status(HttpStatus.BAD_REQUEST).build()
      case error =>
        logger.error(error.getMessage, error)
        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
    }
  }

}

object WebFluxUtils {
  implicit class MapOps(val self: Map[String, String]) extends AnyVal {
    def required(key: String): Try[String] =
      self.get(key) match {
        case Some(value) => Success(value)
        case None => Failure(ValidationFailureException(s"Required argument not present, key=$key"))
      }

    def required[T](key: String, mapper: String => Try[T]): Try[T] =
      required(key).flatMap(mapper)

    def optional(key: String): Try[Option[String]] =
      self.get(key).map(_.asRight.toTry).sequence

    def optional[T](key: String, mapper: String => Try[T]): Try[Option[T]] =
      self.get(key).map(mapper).sequence
  }

  implicit class TryOps[T](val self: Try[IO[Either[Throwable, T]]]) extends AnyVal {
    def asJsonResponse(implicit encoder: Encoder[T]): IO[Some[String]] =
      IO.fromTry(self)
        .handleErrorWith(error => IO.raiseError(ValidationFailureException(error.getMessage)))
        .flatten
        .flatMap((result: Either[Throwable, T]) => IO.fromEither(result))
        .map(dto => Some(dto.asJson.deepDropNullValues.noSpaces))

  }

  implicit class ListOps(val self: List[RouterFunction[ServerResponse]]) extends AnyVal {
    def reduceRouters: RouterFunction[ServerResponse] = self.reduceLeft[RouterFunction[ServerResponse]] {
      case (first, second) =>
        first.andOther(second).asInstanceOf[RouterFunction[ServerResponse]]
    }
  }

}
