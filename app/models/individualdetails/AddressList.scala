/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models.individualdetails

import play.api.libs.json.{Format, Json}

final case class AddressList(address: Option[List[Address]])

object AddressList {
  implicit class AddressListOps(private val addressList: AddressList) extends AnyVal {
    def getAddress: List[Address] = addressList.address match {
      case Some(addList) => addList
      case _ => List.empty[Address]
    }
  }

  implicit val format: Format[AddressList] = Json.format[AddressList]
}
