package models

case class Service(name: String, price: BigDecimal, categoryId: Long, active: Boolean, id: Long = 0)
