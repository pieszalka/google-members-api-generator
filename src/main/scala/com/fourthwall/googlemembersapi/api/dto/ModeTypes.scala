package com.fourthwall.googlemembersapi.api.dto

object ModeTypes {

  sealed abstract class ModeType(val value : String) {
    override def toString: String = value
  }

  case object ALL_CURRENT_MODE_TYPE extends ModeType("all_current")
  case object UPDATES_MODE_TYPE extends ModeType("updates")
}
