package com.example.tictactoe.parser.menu

import com.example.tictactoe.domain.{ AppError, MenuCommand }
import zio.{ Has, IO, ZIO }

trait MenuCommandParser {
  def parse(input: String): IO[AppError, MenuCommand]
}
object MenuCommandParser {
  def parse(input: String): ZIO[Has[MenuCommandParser], AppError, MenuCommand] =
    ZIO.serviceWith[MenuCommandParser](_.parse(input))
}
