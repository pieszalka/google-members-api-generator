package com.fourthwall.googlemembersapi.api.dto

case class MembershipLevelListDto(
  kind: String,
  etag: String,
  items: List[MembershipLevelDto]
)
