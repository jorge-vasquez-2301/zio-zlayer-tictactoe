package com.example.tictactoe.parser.game

import com.example.tictactoe.domain.{ AppError, GameCommand }
import zio._

trait GameCommandParser {
  def parse(input: String): IO[AppError, GameCommand]
}

object GameCommandParser {
  def parse(input: String): ZIO[GameCommandParser, AppError, GameCommand] =
    ZIO.serviceWithZIO[GameCommandParser](_.parse(input))
}
