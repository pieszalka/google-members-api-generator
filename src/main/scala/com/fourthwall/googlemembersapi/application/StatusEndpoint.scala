package com.fourthwall.googlemembersapi.application

import cats.effect.IO
import com.fourthwall.googlemembersapi.infrastructure.WebFluxUtils
import com.fourthwall.googlemembersapi.infrastructure.WebFluxUtils._
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.server.{RouterFunction, ServerResponse}

import scala.util.Success

@Configuration
private class StatusEndpoint() extends WebFluxUtils {

  private val status = endpoint(
    method = HttpMethod.GET,
    path = "/status",
    handler = withoutRequestBody {
      case (_, _) => Success(IO.pure(Right("ok"))).asJsonResponse
    }
  )

  @Bean
  def statusRouter: RouterFunction[ServerResponse] = status

}
