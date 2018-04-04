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
      "id" -> default(longNumber, 0l)
    )(NewServiceForm.apply)(NewServiceForm.unapply)
  }

  def newService() = Action.async { implicit request =>
    categoryRepo.list().map { categories =>
      Ok(views.html.services.form(newServicesForm, categories))
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

  def delete(id: Long) = Action.async { implicit request =>
    repo.delete(id).map(x =>
      if (x)
        Redirect(routes.ServiceController.index).flashing("success" -> "service.deleted")
      else
        NotFound
    )
  }

}

case class NewServiceForm(name: String, price: BigDecimal, categoryId: Long, id: Long) {
  lazy val toService: Service = Service(name, price, categoryId, true, id)
}

object NewServiceForm {
  def fromService(c: Service) = NewServiceForm(c.name, c.price, c.categoryId, c.id)
}
