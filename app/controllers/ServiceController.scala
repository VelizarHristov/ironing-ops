package controllers

import javax.inject._
import models.Service
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import repositories.ServiceRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ServiceController @Inject()(repo: ServiceRepository,
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
      "categoryId" -> number,
      "id" -> default(longNumber, 0l)
    )(NewServiceForm.apply)(NewServiceForm.unapply)
  }

  def newService() = Action { implicit request =>
    Ok(views.html.services.form(newServicesForm))
  }

  def create() = Action.async { implicit request =>
    newServicesForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.services.form(errorForm)))
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

case class NewServiceForm(name: String, price: BigDecimal, categoryId: Int, id: Long) {
  lazy val toService: Service = Service(name, price, categoryId, true, id)
}

object NewServiceForm {
  def fromService(c: Service) = NewServiceForm(c.name, c.price, c.categoryId, c.id)
}
