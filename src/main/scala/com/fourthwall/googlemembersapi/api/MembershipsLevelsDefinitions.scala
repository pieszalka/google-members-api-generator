package com.fourthwall.googlemembersapi.api

import com.fourthwall.googlemembersapi.api.dto.MembershipLevelListDto
import io.circe.generic.auto._
import sttp.model.StatusCodes
import sttp.tapir._
import sttp.tapir.json.circe.TapirJsonCirce

//noinspection TypeAnnotation
trait MembershipsLevelsDefinitions extends StatusCodes with TapirJsonCirce {

  /**
    * The part parameter specifies the membershipsLevel resource properties that the API response will include.
    * The parameter value is a comma-separated list of resource parts. The following list shows the parts
    * that can be retrieved and the quota cost for each part:
    *
    * id: 0
    * snippet: 1
    */
  //private val PartParam = query[PartType]("part").example(SNIPPET_PART)
  private val PartParam = query[String]("part").example("id")

  val baseMembershipsLevelsEndpoint = endpoint
    .errorOut(
      oneOf[QueryError](
        statusMapping(ServiceUnavailable, jsonBody[ServiceUnavailable]),
        statusMapping(NotFound, jsonBody[NotFound]),
        statusMapping(BadRequest, jsonBody[InvalidArgument]),
        statusMapping(Unauthorized, jsonBody[Unauthorized]),
        statusMapping(Forbidden, jsonBody[Forbidden])
      )
    )

  /**
    * List all pricing levels of a creator
    *
    * https://www.googleapis.com/youtube/v3/membershipsLevels?part=id
    *
    */
  val listAllPricingLevelsEndpoint =
    baseMembershipsLevelsEndpoint.get
      .in(PartParam)
      .in("youtube/v3/membershipsLevels")
      .out(jsonBody[MembershipLevelListDto])
}
