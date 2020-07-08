package com.fourthwall.googlemembersapi.api.dto

import java.time.LocalDateTime

case class MembershipsDurationDto(
  memberSince: LocalDateTime,
  memberTotalDurationMonths: Int
)
