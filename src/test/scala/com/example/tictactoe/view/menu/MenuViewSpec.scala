package com.example.tictactoe.view.menu

import com.example.tictactoe.domain.MenuFooterMessage
import zio.test.Assertion._
import zio.test._

object MenuViewSpec extends DefaultRunnableSpec {
  def spec =
    suite("MenuView")(
      suite("header")(
        test("returns ascii art TicTacToe") {
          val result = MenuView.header
          assertM(result)(equalTo(asciiArtTicTacToe))
        }
      ),
      suite("content returns list of commands")(
        test("including resume if suspended") {
          val result = MenuView.content(true)
          assertM(result)(equalTo(suspendedCommands))
        },
        test("excluding resume if not suspended") {
          val result = MenuView.content(false)
          assertM(result)(equalTo(notSuspendedCommands))
        }
      ),
      suite("footer renders Message")(
        test("Empty") {
          val result = MenuView.footer(MenuFooterMessage.Empty)
          assertM(result)(equalTo(emptyMessage))
        },
        test("InvalidCommand") {
          val result = MenuView.footer(MenuFooterMessage.InvalidCommand)
          assertM(result)(equalTo(invalidCommandMessage))
        }
      )
    ).provideCustomLayer(MenuViewLive.layer)

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
