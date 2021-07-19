package com.example.tictactoe.parser.menu

import com.example.tictactoe.domain.{ AppError, MenuCommand, ParseError }
import zio._

final case class MenuCommandParserLive() extends MenuCommandParser {
  override def parse(input: String): IO[AppError, MenuCommand] =
    input match {
      case "new game" => ZIO.succeed(MenuCommand.NewGame)
      case "resume"   => ZIO.succeed(MenuCommand.Resume)
      case "quit"     => ZIO.succeed(MenuCommand.Quit)
      case _          => ZIO.fail(ParseError)
    }
}
object MenuCommandParserLive {
  val layer: ULayer[Has[MenuCommandParser]] = (MenuCommandParserLive.apply _).toLayer
}
