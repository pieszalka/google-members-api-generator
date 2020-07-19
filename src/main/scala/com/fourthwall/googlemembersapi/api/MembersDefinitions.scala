package com.fourthwall.googlemembersapi.api

import com.fourthwall.googlemembersapi.api.dto.MemberListDto
import io.circe.generic.auto._
import sttp.model.StatusCodes
import sttp.tapir._
import sttp.tapir.json.circe.TapirJsonCirce

//noinspection TypeAnnotation
trait MembersDefinitions extends StatusCodes with TapirJsonCirce {

  /**
    * The part parameter specifies the member resource properties that the API response will include.
    * Set the parameter value to snippet. The snippet part has a quota cost of 1 unit.
    */
  //private val PartParam = query[PartType]("part").example(SNIPPET_PART)
  private val PartParam = query[String]("part").example("snippet")

  /**
    * The mode parameter indicates which members will be included in the API response. Set the parameter value to one of the following values:
    *
    * all_current (default) - List current members, from newest to oldest. When this value is used, the end of the list
    *        is reached when the API response does not contain a nextPageToken.
    * updates - List only members that joined or upgraded since the previous API call. Note that the first call starts
    *        a new stream of updates but does not actually return any members. To start retrieving the membership updates,
    *        you need to poll the endpoint using the nextPageToken at your desired frequency.
    *
    * Note that when this value is used, the API response always contains a nextPageToken.
    */
  //private val ModeParam = query[ModeType]("mode").example(ALL_CURRENT_MODE_TYPE)
  private val ModeParam = query[String]("mode").example("all_current")

  /**
    * The maxResults parameter specifies the maximum number of items that should be returned in the result set.
    * Acceptable values are 0 to 1000, inclusive. The default value is 5.
    */
  private val MaxResults = query[Int]("maxResults").example(100)

  /**
    * The pageToken parameter identifies a specific page in the result set that should be returned.
    * The token is specific to the mode used with the original API request, so you cannot use a page token retrieved
    * with one mode to subsequently switch to a different mode.
    */
  private val PageToken = query[String]("pageToken").example("page token")

  /**
    * The hasAccessToLevel parameter value is a level ID that specifies the minimum level that members in the result set should have.
    */
  private val HasAccessToLevel = query[String]("hasAccessToLevel").example("level ID")

  /**
    * The filterByMemberChannelId parameter specifies a comma-separated list of channel IDs that can be used
    * to check the membership status of specific users. For example, UC_1,UC_2,UC_3.
    * A maximum of 100 channels can be specified per call.
    */
  private val FilterByMemberChanelId = query[String]("filterByMemberChannelId").example("UC_1,UC_2,UC_3")

  val baseMembersEndpoint = endpoint
    .errorOut(
      oneOf[QueryError](
        statusMapping(ServiceUnavailable, jsonBody[ServiceUnavailable]),
        statusMapping(NotFound, jsonBody[NotFound]),
        statusMapping(BadRequest, jsonBody[InvalidArgument]),
        statusMapping(Unauthorized, jsonBody[Unauthorized]),
        statusMapping(Forbidden, jsonBody[Forbidden])
      )
    )
    .in(auth.bearer)

  /**
    * List all existing members page by page (all levels)
    *
    * https://www.googleapis.com/youtube/v3/members?part=snippet&maxResults=10
    */
  val listAllMembersEndpoint =
    baseMembersEndpoint.get
      .in(PartParam)
      .in(MaxResults)
      .in("youtube/v3/members")
      .out(jsonBody[MemberListDto])

  /**
    * Check users for their memberships status
    *
    * https://www.googleapis.com/youtube/v3/members?part=snippet&filterByMemberChannelId=UC5IKUmQWDT6Fh9YL9lMRyHQ
    */
  val checkUsersForTheirMembershipsEndpoint =
    baseMembersEndpoint.get
      .in(PartParam)
      .in(FilterByMemberChanelId)
      .in("youtube/v3//members")
      .out(jsonBody[MemberListDto])

  /**
    * List members updates
    *
    * https://www.googleapis.com/youtube/v3/members?part=snippet&mode=updates
    */
  val listMembersUpdatesEndpoint =
    baseMembersEndpoint.get
      .in(PartParam)
      .in(ModeParam)
      .in("youtube/v3///members")
      .out(jsonBody[MemberListDto])

  /**
    * List members
    *
    * https://www.googleapis.com/youtube/v3/members?...
    */
  val listMembersEndpoint =
    baseMembersEndpoint.get
      .in(PartParam)
      .in(MaxResults)
      .in(FilterByMemberChanelId)
      .in(ModeParam)
      .in(PageToken)
      .in(HasAccessToLevel)
      .in("youtube/v3////members")
      .out(jsonBody[MemberListDto])
}
