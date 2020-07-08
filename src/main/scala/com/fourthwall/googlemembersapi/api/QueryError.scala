package com.fourthwall.googlemembersapi.api

sealed trait QueryError extends Exception

final case class ServiceUnavailable(error: Error) extends Exception with QueryError
final case class NotFound(error: Error) extends Exception with QueryError
final case class InvalidArgument(error: Error) extends Exception with QueryError
final case class Unauthorized(error: Error) extends Exception with QueryError
final case class Forbidden(error: Error) extends Exception with QueryError

case class ValidationFailureException(reason: String) extends IllegalArgumentException(reason)

case class Error(
  message: String,
  code: Int,
  errors: List[ErrorDetails]
)

case class ErrorDetails(
  reason: String,
  locationType: String,
  message: String,
  domain: String,
  location: String
)
