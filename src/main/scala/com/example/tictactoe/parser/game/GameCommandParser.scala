package com.example.tictactoe.parser.game

import com.example.tictactoe.domain.{ AppError, GameCommand }
import zio.{ Has, IO, ZIO }

trait GameCommandParser {
  def parse(input: String): IO[AppError, GameCommand]
}
object GameCommandParser {
  def parse(input: String): ZIO[Has[GameCommandParser], AppError, GameCommand] =
    ZIO.serviceWith[GameCommandParser](_.parse(input))
}
