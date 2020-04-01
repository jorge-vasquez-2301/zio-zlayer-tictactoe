package com.example.tictactoe.view.menu

import com.example.tictactoe.domain.MenuFooterMessage
import zio.test.Assertion._
import zio.test._

object MenuViewSpec extends DefaultRunnableSpec {
  def spec =
    suite("MenuView")(
      suite("header")(
        testM("returns ascii art TicTacToe") {
          val result = MenuView.header
          assertM(result)(equalTo(asciiArtTicTacToe))
        }
      ),
      suite("content returns list of commands")(
        testM("including resume if suspended") {
          val result = MenuView.content(true)
          assertM(result)(equalTo(suspendedCommands))
        },
        testM("excluding resume if not suspended") {
          val result = MenuView.content(false)
          assertM(result)(equalTo(notSuspendedCommands))
        }
      ),
      suite("footer renders Message")(
        testM("Empty") {
          val result = MenuView.footer(MenuFooterMessage.Empty)
          assertM(result)(equalTo(emptyMessage))
        },
        testM("InvalidCommand") {
          val result = MenuView.footer(MenuFooterMessage.InvalidCommand)
          assertM(result)(equalTo(invalidCommandMessage))
        }
      )
    ).provideCustomLayer(MenuView.Service.live)

  private val asciiArtTicTacToe =
    """ _____  _        _____               _____              
      #/__   \(_)  ___ /__   \  __ _   ___ /__   \  ___    ___ 
      #  / /\/| | / __|  / /\/ / _` | / __|  / /\/ / _ \  / _ \
      # / /   | || (__  / /   | (_| || (__  / /   | (_) ||  __/
      # \/    |_| \___| \/     \__,_| \___| \/     \___/  \___|""".stripMargin('#')

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
