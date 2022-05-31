package com.example.tictactoe.parser.menu

import com.example.tictactoe.domain.{ MenuCommand, ParseError }
import zio.test._

object MenuCommandParserSpec extends ZIOSpecDefault {
  def spec = suite("MenuCommandParser")(
    suite("parse")(
      test("new game returns NewGame command") {
        for {
          result <- MenuCommandParser.parse("new game").either.right
        } yield assertTrue(result == MenuCommand.NewGame)
      },
      test("resume returns Resume command") {
        for {
          result <- MenuCommandParser.parse("resume").either.right
        } yield assertTrue(result == MenuCommand.Resume)
      },
      test("quit returns Quit command") {
        for {
          result <- MenuCommandParser.parse("quit").either.right
        } yield assertTrue(result == MenuCommand.Quit)
      },
      test("any other input fails") {
        check(invalidCommandsGen) { input =>
          for {
            result <- MenuCommandParser.parse(input).either.left
          } yield assertTrue(result == ParseError)
        }
      }
    ).provideLayer(MenuCommandParserLive.layer)
  )

  private val validCommands      = List("new game", "resume", "quit")
  private val invalidCommandsGen = Gen.string.filter(!validCommands.contains(_))
}
