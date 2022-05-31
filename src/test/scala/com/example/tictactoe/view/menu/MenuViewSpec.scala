package com.example.tictactoe.view.menu

import com.example.tictactoe.domain.MenuFooterMessage
import zio.test.Assertion._
import zio.test._

object MenuViewSpec extends ZIOSpecDefault {
  def spec =
    suite("MenuView")(
      suite("header")(
        test("returns ascii art TicTacToe") {
          for {
            result <- MenuView.header
          } yield assertTrue(result == asciiArtTicTacToe)
        }
      ),
      suite("content returns list of commands")(
        test("including resume if suspended") {
          for {
            result <- MenuView.content(true)
          } yield assertTrue(result == suspendedCommands)
        },
        test("excluding resume if not suspended") {
          for {
            result <- MenuView.content(false)
          } yield assertTrue(result == notSuspendedCommands)
        }
      ),
      suite("footer renders Message")(
        test("Empty") {
          for {
            result <- MenuView.footer(MenuFooterMessage.Empty)
          } yield assertTrue(result == emptyMessage)
        },
        test("InvalidCommand") {
          for {
            result <- MenuView.footer(MenuFooterMessage.InvalidCommand)
          } yield assertTrue(result == invalidCommandMessage)
        }
      )
    ).provideLayer(MenuViewLive.layer)

  private val asciiArtTicTacToe =
    """
      | _____   __                             _______     ______          ______
      |/__  /  / /   ____ ___  _____  _____   /_  __(_)___/_  __/___ _____/_  __/___  ___
      |  / /  / /   / __ `/ / / / _ \/ ___/    / / / / ___// / / __ `/ ___// / / __ \/ _ \
      | / /__/ /___/ /_/ / /_/ /  __/ /       / / / / /__ / / / /_/ / /__ / / / /_/ /  __/
      |/____/_____/\__,_/\__, /\___/_/       /_/ /_/\___//_/  \__,_/\___//_/  \____/\___/
      |                 /____/
      |""".stripMargin

  private val suspendedCommands =
    """* new game
      |* resume
      |* quit""".stripMargin

  private val notSuspendedCommands =
    """* new game
      |* quit""".stripMargin

  private val emptyMessage          = ""
  private val invalidCommandMessage = "Invalid command. Try again."
}
