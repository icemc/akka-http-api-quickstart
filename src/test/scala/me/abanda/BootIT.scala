package me.abanda

import me.abanda.Boot
import me.abanda.utils.InMemoryPostgresStorage
import sttp.client3._
import sttp.client3.akkahttp._

class BootIT extends BaseServiceTest {

//  InMemoryPostgresStorage
  val sttpBackend = AkkaHttpBackend()

  "Service" should {

    "bind on port successfully and answer on health checks" in {
      awaitForResult(for {
        serverBinding <- Boot.startApplication()
        healthCheckResponse <- basicRequest.get(uri"http://localhost:9000/healthcheck").send(sttpBackend)
        _ <- serverBinding.unbind()
      } yield {
        healthCheckResponse.isSuccess shouldBe true
        healthCheckResponse.body shouldBe Right("OK")
      })
    }

  }

}
