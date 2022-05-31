package com.example.tictactoe.view.confirm

import com.example.tictactoe.domain.{ ConfirmAction, ConfirmFooterMessage }
import zio.test.Assertion._
import zio.test._

object ConfirmViewSpec extends ZIOSpecDefault {
  def spec =
    suite("ConfirmView")(
      suite("header returns action description")(
        test("NewGame") {
          for {
            result <- ConfirmView.header(ConfirmAction.NewGame)
          } yield assertTrue(result == newGameDescription)
        },
        test("Quit") {
          for {
            result <- ConfirmView.header(ConfirmAction.Quit)
          } yield assertTrue(result == quitDescription)
        }
      ),
      suite("content")(
        test("returns confirm prompt") {
          for {
            result <- ConfirmView.content
          } yield assertTrue(result == confirmPrompt)
        }
      ),
      suite("footer renders Message")(
        test("Empty") {
          for {
            result <- ConfirmView.footer(ConfirmFooterMessage.Empty)
          } yield assertTrue(result == emptyMessage)
        },
        test("InvalidCommand") {
          for {
            result <- ConfirmView.footer(ConfirmFooterMessage.InvalidCommand)
          } yield assertTrue(result == invalidCommandMessage)
        }
      )
    ).provideLayer(ConfirmViewLive.layer)

  private val newGameDescription =
    """[New game]
      |
      |This will discard current game progress.""".stripMargin

  private val quitDescription =
    """[Quit]
      |
      |This will discard current game progress.""".stripMargin

  private val confirmPrompt =
    """Are you sure?
      |<yes> / <no>""".stripMargin

  private val emptyMessage =
    ""

  private val invalidCommandMessage =
    "Invalid command. Try again."
}
