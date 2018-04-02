package repositories

import javax.inject.{Inject, Singleton}
import models.Category
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class CategoriesTable(tag: Tag) extends Table[Category](tag, "categories") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.Unique)

    def * = (name, id) <>
      ((Category.apply _).tupled, Category.unapply)
  }

  private val categories = TableQuery[CategoriesTable]

  def list(): Future[Seq[Category]] = db.run {
    categories.result
  }

  def byId(id: Long): Future[Option[Category]] = {
    db.run {
      categories.filter(_.id === id).result
    }.map(_.headOption)
  }

  def create(category: Category): Future[Int] = db.run {
    categories += category
  }

  def update(category: Category): Future[Boolean] = {
    db.run {
      categories.filter(_.id === category.id).update(category)
    }.map(_ == 1)
  }

  def delete(id: Long): Future[Boolean] = {
    db.run {
      categories.filter(_.id === id).delete
    }.map(_ == 1)
  }
}
