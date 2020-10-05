package com.example.tictactoe

import zio._
import zio.console._
import zio.macros.accessible

package object terminal {
  type Terminal = Has[Terminal.Service]

  @accessible
  object Terminal {
    trait Service {
      val getUserInput: UIO[String]
      def display(frame: String): UIO[Unit]
    }
    val ansiClearScreen: String = "\u001b[H\u001b[2J"

    val live: URLayer[Console, Terminal] = ZLayer.fromService { consoleService =>
      new Service {
        override val getUserInput: UIO[String] = consoleService.getStrLn.orDie
        override def display(frame: String): UIO[Unit] =
          consoleService.putStr(ansiClearScreen) *> consoleService.putStrLn(frame)
      }
    }
  }
}
