package controllers

import javax.inject._
import models.Category
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import repositories.CategoryRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryController @Inject()(repo: CategoryRepository,
                                   cc: MessagesControllerComponents,
                                   implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {
  def index() = Action.async { implicit request =>
    repo.list().map { categories =>
      Ok(views.html.categories.index(categories))
    }
  }

  val categoriesForm = Form {
    mapping(
      "name" -> nonEmptyText,
      "id" -> default(longNumber, 0l)
    )(CategoryForm.apply)(CategoryForm.unapply)
  }

  def newCategory() = Action { implicit request =>
    Ok(views.html.categories.form(categoriesForm))
  }

  def edit(id: Long) = Action.async { implicit request =>
    repo.byId(id).map {
      case Some(category) => Ok(views.html.categories.form(
        categoriesForm.fill(CategoryForm.fromCategory(category))
      ))
      case None => NotFound
    }
  }

  def create() = Action.async { implicit request =>
    categoriesForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.categories.form(errorForm)))
      },
      form => {
        repo.create(form.toCategory).map(_ =>
          Redirect(routes.CategoryController.index).flashing("success" -> "category.created")
        )
      }
    )
  }

  def update() = Action.async { implicit request =>
    categoriesForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.categories.form(errorForm)))
      },
      form => {
        repo.update(form.toCategory).map(_ =>
          Redirect(routes.CategoryController.index).flashing("success" -> "category.updated")
        )
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request =>
    repo.delete(id).map(x =>
      if (x)
        Redirect(routes.CategoryController.index).flashing("success" -> "category.deleted")
      else
        NotFound
    )
  }

}

case class CategoryForm(name: String, id: Long) {
  lazy val toCategory: Category = Category(name, id)
}

object CategoryForm {
  def fromCategory(c: Category) = CategoryForm(c.name, c.id)
}
