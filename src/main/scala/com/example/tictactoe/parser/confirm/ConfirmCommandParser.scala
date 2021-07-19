package com.example.tictactoe.parser.confirm

import com.example.tictactoe.domain.{ AppError, ConfirmCommand }
import zio.{ Has, IO, ZIO }

trait ConfirmCommandParser {
  def parse(input: String): IO[AppError, ConfirmCommand]
}
object ConfirmCommandParser {
  def parse(input: String): ZIO[Has[ConfirmCommandParser], AppError, ConfirmCommand] =
    ZIO.serviceWith[ConfirmCommandParser](_.parse(input))
}
