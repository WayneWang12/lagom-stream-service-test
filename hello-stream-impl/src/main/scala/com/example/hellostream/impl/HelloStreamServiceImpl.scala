package com.example.hellostream.impl

import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.example.hellostream.api.HelloStreamService
import com.example.hello.api.HelloService

import scala.concurrent.Future

/**
  * Implementation of the HelloStreamService.
  */
class HelloStreamServiceImpl(helloService: HelloService) extends HelloStreamService {
  def stream = ServiceCall { hellos =>
    val source = hellos.flatMapConcat{ hello =>
      Source.unfoldAsync(0) {offset =>
        if(offset < 10) Future.successful(Some(offset + 1, Seq(s"Wayne$offset")))
        else Future.successful(None)
      }
    }
    Future.successful(source.mapConcat(_.to[scala.collection.immutable.Iterable]))
  }
}
