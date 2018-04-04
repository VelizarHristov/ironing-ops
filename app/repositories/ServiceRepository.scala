package repositories

import javax.inject.{Inject, Singleton}
import models.Service
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ServiceRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class ServicesTable(tag: Tag) extends Table[Service](tag, "services") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def price = column[BigDecimal]("price")
    def categoryId = column[Int]("category_id")
    def active = column[Boolean]("active")

    def * = (name, price, categoryId, active, id) <>
      ((Service.apply _).tupled, Service.unapply)
  }

  private val services = TableQuery[ServicesTable]

  def list(): Future[Seq[Service]] = db.run {
    services.result
  }

  def byId(id: Long): Future[Option[Service]] = {
    db.run {
      services.filter(_.id === id).result
    }.map(_.headOption)
  }

  def create(service: Service): Future[Int] = db.run {
    services += service
  }

  def update(service: Service): Future[Boolean] = {
    db.run {
      services.filter(_.id === service.id).update(service)
    }.map(_ == 1)
  }

  def delete(id: Long): Future[Boolean] = {
    db.run {
      services.filter(_.id === id).delete
    }.map(_ == 1)
  }
}
