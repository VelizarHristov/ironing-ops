package models

case class Customer(nickname: String, email: String, firstName: String, lastName: String,
                    phoneHome: String, phoneMobile: String, notes: String, paymentScheme: Int,
                    id: Long = 0) {
  val paymentSchemeString = if (paymentScheme == 0) "Flat rate" else "Itemized"
}
