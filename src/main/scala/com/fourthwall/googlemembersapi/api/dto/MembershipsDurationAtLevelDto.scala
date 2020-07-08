package com.fourthwall.googlemembersapi.api.dto

import java.time.LocalDateTime

case class MembershipsDurationAtLevelDto(
  level: String,
  memberSince: LocalDateTime,
  memberTotalDurationMonths: Int
)
