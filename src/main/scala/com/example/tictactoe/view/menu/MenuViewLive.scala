package com.example.tictactoe.view.menu

import com.example.tictactoe.domain.MenuFooterMessage
import zio._

final case class MenuViewLive() extends MenuView {
  val header: UIO[String] =
    ZIO.succeed(
      """
        | _____   __                             _______     ______          ______
        |/__  /  / /   ____ ___  _____  _____   /_  __(_)___/_  __/___ _____/_  __/___  ___
        |  / /  / /   / __ `/ / / / _ \/ ___/    / / / / ___// / / __ `/ ___// / / __ \/ _ \
        | / /__/ /___/ /_/ / /_/ /  __/ /       / / / / /__ / / / /_/ / /__ / / / /_/ /  __/
        |/____/_____/\__,_/\__, /\___/_/       /_/ /_/\___//_/  \__,_/\___//_/  \____/\___/
        |                 /____/
        |""".stripMargin
    )
  def content(isSuspended: Boolean): UIO[String] =
    ZIO.succeed {
      val commands =
        if (isSuspended) List("new game", "resume", "quit")
        else List("new game", "quit")
      commands
        .map(cmd => s"* $cmd")
        .mkString("\n")
    }
  def footer(message: MenuFooterMessage): UIO[String] =
    ZIO.succeed(message) map {
      case MenuFooterMessage.Empty          => ""
      case MenuFooterMessage.InvalidCommand => "Invalid command. Try again."
    }
}
object MenuViewLive {
  val layer: ULayer[MenuView] = ZLayer.succeed(MenuViewLive())
}
