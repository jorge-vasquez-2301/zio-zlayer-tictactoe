package com.example.tictactoe.parser

import com.example.tictactoe.domain.MenuCommand
import zio._

package object menu {
  type MenuCommandParser = Has[MenuCommandParser.Service]
  object MenuCommandParser {
    trait Service {
      def parse(input: String): IO[Unit, MenuCommand]
    }
    object Service {
      val live: Layer[Nothing, MenuCommandParser] = ZLayer.succeed {
        new Service {
          override def parse(input: String): IO[Unit, MenuCommand] =
            input match {
              case "new game" => ZIO.succeed(MenuCommand.NewGame)
              case "resume"   => ZIO.succeed(MenuCommand.Resume)
              case "quit"     => ZIO.succeed(MenuCommand.Quit)
              case _          => ZIO.fail(())
            }
        }
      }
    }

    // accessors
    def parse(input: String): ZIO[MenuCommandParser, Unit, MenuCommand] = ZIO.accessM(_.get.parse(input))
  }
}
