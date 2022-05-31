package com.example.tictactoe.parser.confim

import com.example.tictactoe.domain.{ ConfirmCommand, ParseError }
import com.example.tictactoe.parser.confirm.{ ConfirmCommandParser, ConfirmCommandParserLive }
import zio.test._

object ConfirmCommandParserSpec extends ZIOSpecDefault {
  def spec =
    suite("ConfirmCommandParser")(
      suite("parse")(
        test("yes returns Yes command") {
          for {
            result <- ConfirmCommandParser.parse("yes").either.right
          } yield assertTrue(result == ConfirmCommand.Yes)
        },
        test("no returns No command") {
          for {
            result <- ConfirmCommandParser.parse("no").either.right
          } yield assertTrue(result == ConfirmCommand.No)
        },
        test("any other input returns Invalid command") {
          check(invalidCommandsGen) { input =>
            for {
              result <- ConfirmCommandParser.parse(input).either.left
            } yield assertTrue(result == ParseError)
          }
        }
      )
    ).provideLayer(ConfirmCommandParserLive.layer)

  private val validCommands      = List("yes", "no")
  private val invalidCommandsGen = Gen.string.filter(!validCommands.contains(_))
}
