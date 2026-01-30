package com.example.tictactoe.parser.menu

import com.example.tictactoe.domain.{ AppError, MenuCommand }
import zio._

trait MenuCommandParser {
  def parse(input: String): IO[AppError, MenuCommand]
}

object MenuCommandParser {
  def parse(input: String): ZIO[MenuCommandParser, AppError, MenuCommand] =
    ZIO.serviceWithZIO[MenuCommandParser](_.parse(input))
}
