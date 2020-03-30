package com.example.tictactoe

import zio._
import zio.console._

package object terminal {
  type Terminal = Has[Terminal.Service]

  object Terminal {
    trait Service {
      val getUserInput: UIO[String]
      def display(frame: String): UIO[Unit]
    }
    object Service {
      val ansiClearScreen: String = "\u001b[H\u001b[2J"

      val live: ZLayer[Console, Nothing, Terminal] = ZLayer.fromFunction { console: Console =>
        new Service {
          override val getUserInput: UIO[String] = console.get.getStrLn.orDie

          override def display(frame: String): UIO[Unit] =
            for {
              _ <- console.get.putStr(ansiClearScreen)
              _ <- console.get.putStrLn(frame)
            } yield ()
        }
      }
    }

    // accessors
    val getUserInput: URIO[Terminal, String]         = ZIO.accessM(_.get.getUserInput)
    def display(frame: String): URIO[Terminal, Unit] = ZIO.accessM(_.get.display(frame))
  }
}
