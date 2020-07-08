package com.fourthwall.googlemembersapi.api.dto

case class MemberSnippetDto(
  creatorChannelId: String,
  memberDetails: MemberDetailsDto,
  membershipsDetails: MembershipsDetailsDto
)
