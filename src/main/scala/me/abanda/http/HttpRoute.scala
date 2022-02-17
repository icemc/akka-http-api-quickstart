package me.abanda.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import me.abanda.core.profiles.UserProfileService
import me.abanda.http.routes.{ AuthRoute, ProfileRoute }
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import me.abanda.core.auth.AuthService

import scala.concurrent.ExecutionContext

class HttpRoute(
    userProfileService: UserProfileService,
    authService: AuthService,
    secretKey: String
)(implicit executionContext: ExecutionContext) {

  private val usersRouter = new ProfileRoute(secretKey, userProfileService)
  private val authRouter  = new AuthRoute(authService)

  val route: Route =
    cors() {
      pathPrefix("v1") {
        usersRouter.route ~
        authRouter.route
      } ~
      pathPrefix("healthcheck") {
        get {
          complete("OK")
        }
      }
    }

}
