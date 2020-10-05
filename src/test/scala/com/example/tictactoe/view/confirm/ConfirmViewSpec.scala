package com.example.tictactoe.view.confirm

import com.example.tictactoe.domain.{ ConfirmAction, ConfirmFooterMessage }
import zio.test.Assertion._
import zio.test._

object ConfirmViewSpec extends DefaultRunnableSpec {
  def spec =
    suite("ConfirmView")(
      suite("header returns action description")(
        testM("NewGame") {
          val result = ConfirmView.header(ConfirmAction.NewGame)
          assertM(result)(equalTo(newGameDescription))
        },
        testM("Quit") {
          val result = ConfirmView.header(ConfirmAction.Quit)
          assertM(result)(equalTo(quitDescription))
        }
      ),
      suite("content")(
        testM("returns confirm prompt") {
          val result = ConfirmView.content
          assertM(result)(equalTo(confirmPrompt))
        }
      ),
      suite("footer renders Message")(
        testM("Empty") {
          val result = ConfirmView.footer(ConfirmFooterMessage.Empty)
          assertM(result)(equalTo(emptyMessage))
        },
        testM("InvalidCommand") {
          val result = ConfirmView.footer(ConfirmFooterMessage.InvalidCommand)
          assertM(result)(equalTo(invalidCommandMessage))
        }
      )
    ).provideCustomLayer(ConfirmView.live)

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
