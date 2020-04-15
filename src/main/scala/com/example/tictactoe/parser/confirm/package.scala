package com.example.tictactoe.parser

import com.example.tictactoe.domain.{ AppError, ConfirmCommand, ParseError }
import zio._

package object confirm {
  type ConfirmCommandParser = Has[ConfirmCommandParser.Service]

  object ConfirmCommandParser {
    trait Service {
      def parse(input: String): IO[AppError, ConfirmCommand]
    }
    val live: ULayer[ConfirmCommandParser] = ZLayer.succeed {
      new Service {
        override def parse(input: String): IO[AppError, ConfirmCommand] =
          input match {
            case "yes" => ZIO.succeed(ConfirmCommand.Yes)
            case "no"  => ZIO.succeed(ConfirmCommand.No)
            case _     => ZIO.fail(ParseError)
          }
      }
    }

    val dummy: ULayer[ConfirmCommandParser] = ZLayer.succeed {
      new Service {
        override def parse(input: String): IO[AppError, ConfirmCommand] = IO.fail(ParseError)
      }
    }

    // accessors
    def parse(input: String): ZIO[ConfirmCommandParser, AppError, ConfirmCommand] = ZIO.accessM(_.get.parse(input))
  }
}
