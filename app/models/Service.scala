package models

import java.sql.Timestamp

case class Service(name: String, price: BigDecimal, categoryId: Long, active: Boolean, expiredAt: Option[Timestamp], id: Long = 0)
