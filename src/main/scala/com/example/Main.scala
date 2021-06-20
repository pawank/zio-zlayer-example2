package com.example

import zio.{ App, ExitCode, URIO, ZEnv, ZIO }
import zio.console.{ getStrLn, putStrLn, Console }
import com.example.service._
import dependencyviaservice._
import dependencyviamacros._

object Main extends App {
  val env = Console.live ++ (Console.live >>> DependencyViaService.live) ++ DependencyViaMacros.live
  val app: ZIO[Console with DependencyViaService with DependencyViaMacros, Throwable, Unit] = {
    val result = 
      for {
        _    <- putStrLn("What is your name?")
        name <- getStrLn
        ex1 <- dependencyviaservice.hello(name)
        svcname <- ex1
        ex2 <- dependencyviamacros.DependencyViaMacros.hello(name)
        out  <- putStrLn(s"Hello $name! $svcname and $ex2")
      } yield out
    result
  }

  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    app.provideCustomLayer(env).exitCode
}