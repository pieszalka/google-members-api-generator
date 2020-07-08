package com.fourthwall.googlemembersapi.api.dto

case class MemberDto(
  kind: String,
  etag: String,
  snippet: MemberSnippetDto
)
