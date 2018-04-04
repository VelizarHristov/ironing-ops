package models

case class Service(name: String, price: BigDecimal, categoryId: Int, active: Boolean, id: Long = 0)
