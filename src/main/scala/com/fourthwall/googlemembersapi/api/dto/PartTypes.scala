package com.fourthwall.googlemembersapi.api.dto

import sttp.tapir.Codec.PlainCodec
import sttp.tapir.{Codec, DecodeResult, Validator}

object PartTypes {

  sealed abstract class PartType(val value: String) {
    override def toString: String = value
  }

  case object SNIPPET_PART extends PartType("snippet")
  case object ID_PART extends PartType("id")

  def parse(value: String): Either[Throwable, PartType] = value match {
    case SNIPPET_PART.value => Right(SNIPPET_PART)
    case ID_PART.value => Right(ID_PART)
    case _ => Left(new RuntimeException())
  }

  def decode(s: String): DecodeResult[PartType] = PartTypes.parse(s) match {
    case Right(v) => DecodeResult.Value(v)
    case Left(f) => DecodeResult.Error(s, f)
  }

  def encode(part: PartType): String = part.toString.toLowerCase

  implicit val partCodec: PlainCodec[PartType] = Codec.stringPlainCodecUtf8.mapDecode(decode)(encode)

  implicit def partValidator: Validator[PartType] = Validator.enum.encode(_.toString.toLowerCase)
}
