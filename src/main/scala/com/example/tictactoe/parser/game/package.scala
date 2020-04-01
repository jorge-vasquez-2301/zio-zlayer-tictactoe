package com.example.tictactoe.parser

import atto.Atto._
import atto.Parser
import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.GameCommand
import zio._

package object game {
  type GameCommandParser = Has[GameCommandParser.Service]
  object GameCommandParser {
    trait Service {
      def parse(input: String): IO[Unit, GameCommand]
    }
    object Service {
      val live: ULayer[GameCommandParser] = ZLayer.succeed {
        new Service {
          override def parse(input: String): IO[Unit, GameCommand] =
            ZIO.fromOption(command.parse(input).done.option)

          private lazy val command: Parser[GameCommand] =
            choice(menu, put)

          private lazy val menu: Parser[GameCommand] =
            (string("menu") <~ endOfInput) >| GameCommand.Menu

          private lazy val put: Parser[GameCommand] =
            (int <~ endOfInput).flatMap { value =>
              Field
                .make(value)
                .fold(err[GameCommand](s"Invalid field value: $value"))(field =>
                  ok(field).map(field => GameCommand.Put(field))
                )
            }
        }
      }
    }

    // accessors
    def parse(input: String): ZIO[GameCommandParser, Unit, GameCommand] = ZIO.accessM(_.get.parse(input))
  }
}
