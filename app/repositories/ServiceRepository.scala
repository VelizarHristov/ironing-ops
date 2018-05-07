package repositories

import java.sql.Date
import javax.inject.{Inject, Singleton}
import models.{Service, Category}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ServiceRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                  categoryRepository: CategoryRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  import categoryRepository.categories

  private class ServicesTable(tag: Tag) extends Table[Service](tag, "services") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def price = column[BigDecimal]("price")
    def categoryId = column[Long]("category_id")
    def active = column[Boolean]("active")
    def expiredAt = column[Option[Date]]("expired_at")

    def * = (name, price, categoryId, active, expiredAt, id) <>
      ((Service.apply _).tupled, Service.unapply)
  }

  private val services = TableQuery[ServicesTable]

  def list(): Future[Seq[(Service, Category)]] = db.run {
    services.join(categories).on(_.categoryId === _.id).result
  }

  def byId(id: Long): Future[Option[Service]] = {
    db.run {
      services.filter(_.id === id).result
    }.map(_.headOption)
  }

  def create(service: Service): Future[Int] = db.run {
    services += service
  }

  def update(id: Long, name: String, categoryId: Long): Future[Boolean] = {
    db.run {
      services.filter(_.id === id).map(s => (s.name, s.categoryId)).update(name, categoryId)
    }.map(_ == 1)
  }

  def updatePrice(id: Long, newPrice: BigDecimal): Future[Boolean] = {
    Future(true)
  }

  def delete(id: Long): Future[Boolean] = {
    db.run {
      services.filter(_.id === id).delete
    }.map(_ == 1)
  }
}
