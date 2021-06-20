package com.example.service

import zio._
import zio.console._

object dependencyviaservice {
  type DependencyViaService = Has[DependencyViaService.Service]

  object DependencyViaService {

    trait Service {
      def hello(name: String): ZIO[Console, Throwable, String]
    }

    object Service {
      val live: Service = new Service {
        def hello(name: String): ZIO[Console, Throwable, String] = {
          putStrLn(s"Hello from service: $name") *> ZIO.succeed(s"Hello from service $name")
        }
      }
    }

    //All ways are same
    //val live: ULayer[Has[Service]] = ZLayer.succeed(DependencyViaService.Service.live)
    //val live: ZLayer[Console, Throwable, Has[Service]] = ZLayer.succeed(DependencyViaService.Service.live)
    val live: ZLayer[Console, Throwable, DependencyViaService] = ZLayer.succeed(DependencyViaService.Service.live)
  }


  def hello(name: String) = ZIO.access[DependencyViaService](_.get.hello(name))
  //def hello(name: String): ZIO[Console with DependencyViaService, Throwable, zio.ZIO[zio.console.Console, Throwable, String]] = ZIO.access[DependencyViaService](_.get.hello(name))
}


object dependencyviamacros {
  import zio.macros.accessible
  import zio.macros.throwing

  type DependencyViaMacros = Has[DependencyViaMacros.Service]

  @accessible  
  object DependencyViaMacros {

    trait Service {
      @throwing
      def hello(name: String): ZIO[Any, Throwable, String]
    }
    
    object Service {
      val live: Service = new Service {
        def hello(name: String): ZIO[Any, Throwable, String] = {
          ZIO.succeed(s"Hello from macro service $name")
        }
      }
    }

    val live: ZLayer[Any, Throwable, Has[Service]] = ZLayer.succeed(DependencyViaMacros.Service.live)
  }

  def helloWithoutMacro(name: String) = ZIO.accessM[Has[DependencyViaMacros.Service]](_.get[DependencyViaMacros.Service].hello(name))
}