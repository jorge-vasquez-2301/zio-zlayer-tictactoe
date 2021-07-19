package com.example.tictactoe.parser.menu

import com.example.tictactoe.domain.{ MenuCommand, ParseError }
import zio.test.Assertion._
import zio.test._

object MenuCommandParserSpec extends DefaultRunnableSpec {
  def spec = suite("MenuCommandParser")(
    suite("parse")(
      testM("new game returns NewGame command") {
        val result = MenuCommandParser.parse("new game").either
        assertM(result)(isRight(equalTo(MenuCommand.NewGame)))
      },
      testM("resume returns Resume command") {
        val result = MenuCommandParser.parse("resume").either
        assertM(result)(isRight(equalTo(MenuCommand.Resume)))
      },
      testM("quit returns Quit command") {
        val result = MenuCommandParser.parse("quit").either
        assertM(result)(isRight(equalTo(MenuCommand.Quit)))
      },
      testM("any other input returns Invalid command") {
        checkM(invalidCommandsGen) { input =>
          val result = MenuCommandParser.parse(input).either
          assertM(result)(isLeft(equalTo(ParseError)))
        }
      }
    ).provideCustomLayer(MenuCommandParserLive.layer)
  )

  private val validCommands      = List("new game", "resume", "quit")
  private val invalidCommandsGen = Gen.anyString.filter(!validCommands.contains(_))
}
