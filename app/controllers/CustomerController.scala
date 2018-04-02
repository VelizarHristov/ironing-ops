package controllers

import javax.inject._
import repositories.CustomerRepository
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class CustomerController @Inject()(repo: CustomerRepository,
                                   cc: MessagesControllerComponents,
                                   implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {
  def index() = Action.async { implicit request =>
    repo.list().map { customers =>
      Ok(views.html.customers.index(customers))
    }
  }
}
