package com.example.tictactoe.view.confirm

import com.example.tictactoe.domain.{ ConfirmAction, ConfirmFooterMessage }
import zio._

trait ConfirmView {
  def header(action: ConfirmAction): UIO[String]
  def content: UIO[String]
  def footer(message: ConfirmFooterMessage): UIO[String]
}

object ConfirmView {
  def header(action: ConfirmAction): ZIO[ConfirmView, Nothing, String] =
    ZIO.serviceWithZIO[ConfirmView](_.header(action))

  def content: ZIO[ConfirmView, Nothing, String] =
    ZIO.serviceWithZIO[ConfirmView](_.content)

  def footer(message: ConfirmFooterMessage): ZIO[ConfirmView, Nothing, String] =
    ZIO.serviceWithZIO[ConfirmView](_.footer(message))
}
