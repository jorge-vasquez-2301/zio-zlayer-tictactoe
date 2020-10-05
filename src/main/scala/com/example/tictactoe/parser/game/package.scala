package com.example.tictactoe.parser

import atto.Atto._
import atto.Parser
import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ AppError, GameCommand, ParseError }
import zio._
import zio.macros.accessible

package object game {
  type GameCommandParser = Has[GameCommandParser.Service]

  @accessible
  object GameCommandParser {
    trait Service {
      def parse(input: String): IO[AppError, GameCommand]
    }
    val live: ULayer[GameCommandParser] = ZLayer.succeed {
      new Service {
        override def parse(input: String): IO[AppError, GameCommand] =
          ZIO.fromOption(command.parse(input).done.option).orElseFail(ParseError)

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
    }

    val dummy: ULayer[GameCommandParser] = ZLayer.succeed {
      new Service {
        override def parse(input: String): IO[AppError, GameCommand] = IO.fail(ParseError)
      }
    }
  }
}
