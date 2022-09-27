package me.abanda

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import me.abanda.core.auth.{AuthService, JdbcAuthDataStorage}
import me.abanda.core.profiles.{JdbcUserProfileStorage, UserProfileService}
import me.abanda.http.HttpRoute
import me.abanda.utils.Config
import me.abanda.utils.db.{DatabaseConnector, DatabaseMigrationManager}

import scala.concurrent.{ExecutionContext, Future}

object Boot extends App {

  def startApplication(): Future[Http.ServerBinding] = {
    implicit val actorSystem                     = ActorSystem()
    implicit val executor: ExecutionContext      = actorSystem.dispatcher

    val config = Config.load()

    new DatabaseMigrationManager(
      config.database.jdbcUrl,
      config.database.username,
      config.database.password
    ).migrateDatabaseSchema()

    val databaseConnector = new DatabaseConnector(
      config.database.jdbcUrl,
      config.database.username,
      config.database.password
    )

    val userProfileStorage = new JdbcUserProfileStorage(databaseConnector)
    val authDataStorage    = new JdbcAuthDataStorage(databaseConnector)

    val usersService = new UserProfileService(userProfileStorage)
    val authService  = new AuthService(authDataStorage, config.secretKey)
    val httpRoute    = new HttpRoute(usersService, authService, config.secretKey)

    Http().newServerAt(config.http.host, config.http.port).bind(httpRoute.route)
  }

  startApplication()

}
