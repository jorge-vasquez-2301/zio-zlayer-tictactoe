package com.example.tictactoe.view

import com.example.tictactoe.domain.MenuFooterMessage
import zio._

package object menu {
  type MenuView = Has[MenuView.Service]
  object MenuView {
    trait Service {
      val header: UIO[String]
      def content(isSuspended: Boolean): UIO[String]
      def footer(message: MenuFooterMessage): UIO[String]
    }
    object Service {
      val live: Layer[Nothing, MenuView] = ZLayer.succeed {
        new Service {
          override val header: UIO[String] =
            UIO.succeed(
              """ _____  _        _____               _____              
                #/__   \(_)  ___ /__   \  __ _   ___ /__   \  ___    ___ 
                #  / /\/| | / __|  / /\/ / _` | / __|  / /\/ / _ \  / _ \
                # / /   | || (__  / /   | (_| || (__  / /   | (_) ||  __/
                # \/    |_| \___| \/     \__,_| \___| \/     \___/  \___|""".stripMargin('#')
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
    }

    // accessors
    val header: URIO[MenuView, String]                             = ZIO.accessM(_.get.header)
    def content(isSuspended: Boolean): URIO[MenuView, String]      = ZIO.accessM(_.get.content(isSuspended))
    def footer(message: MenuFooterMessage): URIO[MenuView, String] = ZIO.accessM(_.get.footer(message))
  }
}
