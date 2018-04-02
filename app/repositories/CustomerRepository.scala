package repositories

import javax.inject.{Inject, Singleton}

import models.Customer
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CustomerRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class CustomersTable(tag: Tag) extends Table[Customer](tag, "customers") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def nickname = column[String]("nickname", O.Unique)
    def email = column[String]("email")
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def phoneHome = column[String]("phone_home")
    def phoneMobile = column[String]("phone_mobile")
    def notes = column[String]("notes")
    def paymentScheme = column[Int]("payment_scheme")

    def * = (nickname, email, firstName, lastName, phoneHome, phoneMobile, notes, paymentScheme, id) <>
      ((Customer.apply _).tupled, Customer.unapply)
  }

  private val customers = TableQuery[CustomersTable]

  def list(): Future[Seq[Customer]] = db.run {
    customers.result
  }
}
