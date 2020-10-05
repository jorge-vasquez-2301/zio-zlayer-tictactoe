package com.example.tictactoe.view

import com.example.tictactoe.domain.MenuFooterMessage
import zio._
import zio.macros.accessible

package object menu {
  type MenuView = Has[MenuView.Service]

  @accessible
  object MenuView {
    trait Service {
      val header: UIO[String]
      def content(isSuspended: Boolean): UIO[String]
      def footer(message: MenuFooterMessage): UIO[String]
    }
    val live: ULayer[MenuView] = ZLayer.succeed {
      new Service {
        override val header: UIO[String] =
          UIO.succeed(
            """
              | _____   __                             _______     ______          ______         
              |/__  /  / /   ____ ___  _____  _____   /_  __(_)___/_  __/___ _____/_  __/___  ___ 
              |  / /  / /   / __ `/ / / / _ \/ ___/    / / / / ___// / / __ `/ ___// / / __ \/ _ \
              | / /__/ /___/ /_/ / /_/ /  __/ /       / / / / /__ / / / /_/ / /__ / / / /_/ /  __/
              |/____/_____/\__,_/\__, /\___/_/       /_/ /_/\___//_/  \__,_/\___//_/  \____/\___/ 
              |                 /____/                                                            
              |""".stripMargin
          )

        override def content(isSuspended: Boolean): UIO[String] =
          UIO.succeed {
            val commands =
              if (isSuspended) List("new game", "resume", "quit")
              else List("new game", "quit")

            commands
              .map(cmd => s"* $cmd")
              .mkString("\n")
          }

        override def footer(message: MenuFooterMessage): UIO[String] =
          UIO.succeed(message) map {
            case MenuFooterMessage.Empty          => ""
            case MenuFooterMessage.InvalidCommand => "Invalid command. Try again."
          }
      }
    }

    val dummy: ULayer[MenuView] = ZLayer.succeed {
      new Service {
        override val header: UIO[String]                             = UIO.succeed("")
        override def content(isSuspended: Boolean): UIO[String]      = UIO.succeed("")
        override def footer(message: MenuFooterMessage): UIO[String] = UIO.succeed("")
      }
    }
  }
}
