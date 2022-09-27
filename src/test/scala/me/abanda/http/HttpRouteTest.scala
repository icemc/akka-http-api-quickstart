package me.abanda.http

import akka.http.scaladsl.server.Route
import me.abanda.BaseServiceTest
import me.abanda.core.auth.AuthService
import me.abanda.core.profiles.UserProfileService
import me.abanda.http.HttpRoute

class HttpRouteTest extends BaseServiceTest {

  "HttpRoute" when {

    "GET /healthcheck" should {

      "return 200 OK" in new Context {
        Get("/healthcheck") ~> httpRoute ~> check {
          responseAs[String] shouldBe "OK"
          status.intValue() shouldBe 200
        }
      }

    }

  }

  trait Context {
    val secretKey = "secret"
    val userProfileService: UserProfileService = mock[UserProfileService]
    val authService: AuthService = mock[AuthService]

    val httpRoute: Route = new HttpRoute(userProfileService, authService, secretKey).route
  }

}
