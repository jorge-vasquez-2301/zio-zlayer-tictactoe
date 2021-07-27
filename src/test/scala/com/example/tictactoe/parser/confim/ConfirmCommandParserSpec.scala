package com.example.tictactoe.parser.confim

import com.example.tictactoe.domain.{ ConfirmCommand, ParseError }
import com.example.tictactoe.parser.confirm.{ ConfirmCommandParser, ConfirmCommandParserLive }
import zio.test.Assertion._
import zio.test._

object ConfirmCommandParserSpec extends DefaultRunnableSpec {
  def spec =
    suite("ConfirmCommandParser")(
      suite("parse")(
        test("yes returns Yes command") {
          val result = ConfirmCommandParser.parse("yes")
          assertM(result.either)(isRight(equalTo(ConfirmCommand.Yes)))
        },
        test("no returns No command") {
          val result = ConfirmCommandParser.parse("no")
          assertM(result.either)(isRight(equalTo(ConfirmCommand.No)))
        },
        test("any other input returns Invalid command") {
          checkM(invalidCommandsGen) { input =>
            val result = ConfirmCommandParser.parse(input)
            assertM(result.either)(isLeft(equalTo(ParseError)))
          }
        }
      )
    ).provideCustomLayer(ConfirmCommandParserLive.layer)

  private val validCommands      = List("yes", "no")
  private val invalidCommandsGen = Gen.anyString.filter(!validCommands.contains(_))
}
