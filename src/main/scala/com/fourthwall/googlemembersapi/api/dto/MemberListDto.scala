package com.fourthwall.googlemembersapi.api.dto

case class MemberListDto(
  kind: String,
  etag: String,
  nextPageToken: String,
  pageInfo: PageInfoDto,
  items: List[MemberDto]
)
