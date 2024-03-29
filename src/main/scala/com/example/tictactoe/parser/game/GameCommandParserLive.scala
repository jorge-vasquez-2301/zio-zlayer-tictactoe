package com.example.tictactoe.parser.game

import atto.Atto._
import atto.Parser
import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ AppError, GameCommand, ParseError }
import zio._

final case class GameCommandParserLive() extends GameCommandParser {
  def parse(input: String): IO[AppError, GameCommand] =
    ZIO.from(command.parse(input).done.option).orElseFail(ParseError)
  private lazy val command: Parser[GameCommand] =
    choice(menu, put)
  private lazy val menu: Parser[GameCommand] =
    (string("menu") <~ endOfInput) >| GameCommand.Menu
  private lazy val put: Parser[GameCommand] =
    (int <~ endOfInput).flatMap { value =>
      Field
        .make(value)
        .fold(err[GameCommand](s"Invalid field value: $value"))(field => ok(field).map(GameCommand.Put))
    }
}
object GameCommandParserLive {
  val layer: ULayer[GameCommandParser] = ZLayer.succeed(GameCommandParserLive())
}
