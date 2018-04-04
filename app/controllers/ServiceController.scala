package controllers

import javax.inject._
import models.Service
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import repositories.{CategoryRepository, ServiceRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ServiceController @Inject()(repo: ServiceRepository,
                                  categoryRepo: CategoryRepository,
                                  cc: MessagesControllerComponents,
                                  implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {
  def index() = Action.async { implicit request =>
    repo.list().map { services =>
      Ok(views.html.services.index(services))
    }
  }

  val newServicesForm = Form {
    mapping(
      "name" -> nonEmptyText,
      "price" -> bigDecimal,
      "categoryId" -> longNumber,
    )(NewServiceForm.apply)(NewServiceForm.unapply)
  }

  val editServicesForm = Form {
    mapping(
      "name" -> nonEmptyText,
      "categoryId" -> longNumber,
      "id" -> longNumber
    )(EditServiceForm.apply)(EditServiceForm.unapply)
  }

  def newService() = Action.async { implicit request =>
    categoryRepo.list().map { categories =>
      Ok(views.html.services.form(newServicesForm, categories))
    }
  }

  def edit(id: Long) = Action.async { implicit request =>
    repo.byId(id).flatMap {
      case Some(service) =>
        categoryRepo.list().map { categories =>
          Ok(views.html.services.edit_form(
            editServicesForm.fill(EditServiceForm.fromService(service)),
            categories)
          )
        }
      case None => Future.successful(NotFound)
    }
  }

  def create() = Action.async { implicit request =>
    newServicesForm.bindFromRequest.fold(
      errorForm => {
        categoryRepo.list().map { categories =>
          Ok(views.html.services.form(errorForm, categories))
        }
      },
      form => {
        repo.create(form.toService).map(_ =>
          Redirect(routes.ServiceController.index).flashing("success" -> "service.created")
        )
      }
    )
  }

  def update() = Action.async { implicit request =>
    editServicesForm.bindFromRequest.fold(
      errorForm => {
        categoryRepo.list().map { categories =>
          Ok(views.html.services.edit_form(errorForm, categories))
        }
      },
      form => {
        repo.update(form.id, form.name, form.categoryId).map(_ =>
          Redirect(routes.ServiceController.index).flashing("success" -> "service.updated")
        )
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request =>
    repo.delete(id).map(x =>
      if (x)
        Redirect(routes.ServiceController.index).flashing("success" -> "service.deleted")
      else
        NotFound
    )
  }

}

case class NewServiceForm(name: String, price: BigDecimal, categoryId: Long) {
  lazy val toService: Service = Service(name, price, categoryId, true)
}

object NewServiceForm {
  def fromService(c: Service) = NewServiceForm(c.name, c.price, c.categoryId)
}

case class EditServiceForm(name: String, categoryId: Long, id: Long)

object EditServiceForm {
  def fromService(c: Service) = EditServiceForm(c.name, c.categoryId, c.id)
}
