package com.fourthwall.googlemembersapi.infrastructure

import com.fourthwall.googlemembersapi.api.{MembersDefinitions, MembershipsLevelsDefinitions}
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server
import org.springframework.web.reactive.function.server.{RouterFunction, RouterFunctions, ServerResponse}
import reactor.core.publisher.Mono
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import com.fourthwall.googlemembersapi.infrastructure.WebFluxUtils._

@Configuration
private class OpenApiConfig(
  @Value("classpath:/public/swagger-ui.html") html: Resource
) extends MembersDefinitions with MembershipsLevelsDefinitions {

  private val swaggerYaml = RouterFunctions
    .route()
    .GET(
      "/swagger.yaml",
      (_: server.ServerRequest) =>
        ServerResponse
          .ok()
          .body(
            Mono.just(
              List(
                listAllMembersEndpoint,
                checkUsersForTheirMembershipsEndpoint,
                listMembersUpdatesEndpoint,
                listMembersEndpoint,
                listAllPricingLevelsEndpoint
              ).toOpenAPI("Google Members Api", "0.0.1").toYaml
            ),
            classOf[String]
        )
    )
    .build()

  private val swaggerUi = RouterFunctions
    .route()
    .GET(
      "/swagger-ui.html",
      (_: server.ServerRequest) =>
        ServerResponse
          .ok()
          .contentType(MediaType.TEXT_HTML)
          .syncBody(html)
    )
    .build()

  @Bean
  def swaggerResourcesRouter(): RouterFunction[ServerResponse] =
    List(
      swaggerYaml,
      swaggerUi
    ).reduceRouters
}
