package com.example.tictactoe.view.confirm

import com.example.tictactoe.domain.{ ConfirmAction, ConfirmFooterMessage }
import zio._
import zio.macros._

@accessible
trait ConfirmView {
  def header(action: ConfirmAction): UIO[String]
  def content: UIO[String]
  def footer(message: ConfirmFooterMessage): UIO[String]
}
