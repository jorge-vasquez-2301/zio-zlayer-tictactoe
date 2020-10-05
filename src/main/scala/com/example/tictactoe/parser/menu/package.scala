package com.example.tictactoe.parser

import com.example.tictactoe.domain.{ AppError, MenuCommand, ParseError }
import zio._
import zio.macros.accessible

package object menu {
  type MenuCommandParser = Has[MenuCommandParser.Service]

  @accessible
  object MenuCommandParser {
    trait Service {
      def parse(input: String): IO[AppError, MenuCommand]
    }
    val live: ULayer[MenuCommandParser] = ZLayer.succeed {
      new Service {
        override def parse(input: String): IO[AppError, MenuCommand] =
          input match {
            case "new game" => ZIO.succeed(MenuCommand.NewGame)
            case "resume"   => ZIO.succeed(MenuCommand.Resume)
            case "quit"     => ZIO.succeed(MenuCommand.Quit)
            case _          => ZIO.fail(ParseError)
          }
      }
    }

    val dummy: ULayer[MenuCommandParser] = ZLayer.succeed {
      new Service {
        override def parse(input: String): IO[AppError, MenuCommand] = IO.fail(ParseError)
      }
    }
  }
}
