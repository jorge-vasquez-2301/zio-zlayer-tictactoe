package com.example.tictactoe.parser.confim

import zio.test._
import zio.test.Assertion._
import com.example.tictactoe.domain.ConfirmCommand
import com.example.tictactoe.parser.confirm.ConfirmCommandParser

object ConfirmCommandParserSpec extends DefaultRunnableSpec {
  def spec =
    suite("ConfirmCommandParser")(
      suite("parse")(
        testM("yes returns Yes command") {
          val result = ConfirmCommandParser.parse("yes")
          assertM(result.either)(isRight(equalTo(ConfirmCommand.Yes)))
        },
        testM("no returns No command") {
          val result = ConfirmCommandParser.parse("no")
          assertM(result.either)(isRight(equalTo(ConfirmCommand.No)))
        },
        testM("any other input returns Invalid command") {
          checkM(invalidCommandsGen) { input =>
            val result = ConfirmCommandParser.parse(input)
            assertM(result.either)(isLeft(isUnit))
          }
        }
      )
    ).provideCustomLayer(ConfirmCommandParser.Service.live)

  private val validCommands      = List("yes", "no")
  private val invalidCommandsGen = Gen.anyString.filter(!validCommands.contains(_))
}
