package models

import java.sql.Date

case class Service(name: String, price: BigDecimal, categoryId: Long, active: Boolean, expiredAt: Option[Date], id: Long = 0)
