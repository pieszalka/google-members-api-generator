package com.fourthwall.googlemembersapi.api.dto

object PartTypes {

  sealed abstract class PartType(val value: String) {
    override def toString: String = value
  }

  case object SNIPPET_PART extends PartType("snippet")
  case object ID_PART extends PartType("id")
}
