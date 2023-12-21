/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.findyournationalinsurancenumber.util

object TestData {

  val NinoUser: String =
    """
      |{
      |	"nino": "AA000003B",
      |	"credentialRole": "User",
      |	"internalId": "Int-8612ba91-5581-411d-9d32-fb2de937a565",
      | "confidenceLevel": 250,
      | "affinityGroup": "Individual",
      | "allEnrolments": [],
      | "optionalName" : {"name": "somename"}
      |}
      |""".stripMargin

  val NotLiveNinoUser: String =
    """
      |{
      |	"nino": "ZE000021A",
      |	"credentialRole": "User",
      |	"internalId": "Int-8612ba91-5581-411d-9d32-fb2de937a565",
      | "confidenceLevel": 250,
      | "affinityGroup": "Individual",
      | "allEnrolments": [],
      | "optionalName" : {"name": "somename"}
      |}
      |""".stripMargin

}
