package com.fourthwall.googlemembersapi.api.dto

case class MembershipsDetailsDto(
  highestAccessibleLevel: String,
  highestAccessibleLevelDisplayName: String,
  accessibleLevels: List[String],
  membershipsDuration: MembershipsDurationDto,
  membershipsDurationAtLevel: List[MembershipsDurationAtLevelDto]
)
