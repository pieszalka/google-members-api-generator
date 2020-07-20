package com.fourthwall.googlemembersapi.api.dto

case class MembershipLevelDto(
  kind: String,
  etag: String,
  id: String,
  snippet: Option[MembershipLevelSnippetDto]
)
