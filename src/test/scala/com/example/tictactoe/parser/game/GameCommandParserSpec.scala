package com.example.tictactoe.parser.game

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ GameCommand, ParseError }
import zio._
import zio.test._

object GameCommandParserSpec extends ZIOSpecDefault {
  def spec =
    suite("GameCommandParser")(
      suite("parse")(
        test("menu returns Menu command") {
          for {
            result <- GameCommandParser.parse("menu").either.right
          } yield assertTrue(result == GameCommand.Menu)
        },
        test("number in range 1-9 returns Put command") {
          val results = ZIO.foreach(1 to 9) { n =>
            for {
              result        <- GameCommandParser.parse(s"$n").either.right
              expectedField <- ZIO.from(Field.make(n))
            } yield assertTrue(result == GameCommand.Put(expectedField))
          }
          results.flatMap(results => ZIO.from(results.reduceOption(_ && _)))
        },
        test("invalid command returns error") {
          check(invalidCommandsGen) { input =>
            for {
              result <- GameCommandParser.parse(input).either.left
            } yield assertTrue(result == ParseError)
          }
        }
      )
    ).provideLayer(GameCommandParserLive.layer)

  private val validCommands      = List(1 to 9)
  private val invalidCommandsGen = Gen.string.filter(!validCommands.contains(_))
}
