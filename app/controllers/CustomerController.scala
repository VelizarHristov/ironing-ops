package controllers

import javax.inject._
import models.Customer
import repositories.CustomerRepository
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CustomerController @Inject()(repo: CustomerRepository,
                                   cc: MessagesControllerComponents,
                                   implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {
  def index() = Action.async { implicit request =>
    repo.list().map { customers =>
      Ok(views.html.customers.index(customers))
    }
  }

  val customerForm = Form {
    mapping(
      "nickname" -> nonEmptyText,
      "email" -> nonEmptyText,
      "firstName" -> text,
      "lastName" -> text,
      "phoneHome" -> text,
      "phoneMobile" -> text,
      "paymentScheme" -> number,
      "notes" -> text,
      "id" -> default(longNumber, 0l)
    )(CustomerForm.apply)(CustomerForm.unapply)
  }

  def newCustomer() = Action { implicit request =>
    Ok(views.html.customers.form(customerForm))
  }

  def create() = Action.async { implicit request =>
    customerForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.customers.form(errorForm)))
      },
      form => {
        repo.create(form.toCustomer).map(_ =>
          Redirect(routes.CustomerController.index).flashing("success" -> "customer.created")
        )
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request =>
    repo.delete(id).map(x =>
      if (x)
        Redirect(routes.CustomerController.index).flashing("success" -> "customer.deleted")
      else
        NotFound
    )
  }

}

case class CustomerForm(nickname: String, email: String, firstName: String, lastName: String,
                        phoneHome: String, phoneMobile: String, paymentScheme: Int, notes: String, id: Long) {
  lazy val toCustomer: Customer = Customer(nickname, email, firstName, lastName, phoneHome, phoneMobile, notes, paymentScheme, id = id)
}
