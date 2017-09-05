package com.example.hellostream.impl

import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import com.example.hellostream.api.HelloStreamService
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.AsyncWordSpec

class HelloStreamServiceImplTest extends AsyncWordSpec {
  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra(true)
  ) { ctx =>
    new HelloStreamApplication(ctx) with LocalServiceLocator
  }

  val client = server.serviceClient.implement[HelloStreamService]

  import server.application.materializer

  "Hello stream" should {
    "give expected elements" in {
      val request = Source.single("Hello")
      client.stream.invoke(request).map {response =>
        val probe = response.runWith(TestSink.probe(server.actorSystem))
        probe.request(10)
        probe.expectNext(s"Wayne0")
        probe.expectNext(s"Wayne1")
        probe.expectNext(s"Wayne2")
        probe.expectNext(s"Wayne3")
        probe.expectNext(s"Wayne4")
        probe.cancel()
        succeed
      }
    }
  }


}
