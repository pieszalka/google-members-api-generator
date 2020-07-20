package com.fourthwall.googlemembersapi.api.dto

import sttp.tapir.Codec.PlainCodec
import sttp.tapir.{Codec, DecodeResult, Validator}

object ModeTypes {

  sealed abstract class ModeType(val value: String) {
    override def toString: String = value
  }

  case object ALL_CURRENT_MODE_TYPE extends ModeType("all_current")
  case object UPDATES_MODE_TYPE extends ModeType("updates")

  def parse(value: String): Either[Throwable, ModeType] = value match {
    case ALL_CURRENT_MODE_TYPE.value => Right(ALL_CURRENT_MODE_TYPE)
    case UPDATES_MODE_TYPE.value => Right(UPDATES_MODE_TYPE)
    case _ => Left(new RuntimeException())
  }

  def decode(s: String): DecodeResult[ModeType] = ModeTypes.parse(s) match {
    case Right(v) => DecodeResult.Value(v)
    case Left(f) => DecodeResult.Error(s, f)
  }

  def encode(part: ModeType): String = part.toString.toLowerCase

  implicit val partCodec: PlainCodec[ModeType] = Codec.stringPlainCodecUtf8.mapDecode(decode)(encode)

  implicit def modeValidator: Validator[ModeType] = Validator.enum.encode(_.toString.toLowerCase)
}
