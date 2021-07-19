package com.example.tictactoe.view.confirm

import com.example.tictactoe.domain.{ ConfirmAction, ConfirmFooterMessage }
import zio.{ Has, UIO, URIO, ZIO }

trait ConfirmView {
  def header(action: ConfirmAction): UIO[String]
  def content: UIO[String]
  def footer(message: ConfirmFooterMessage): UIO[String]
}
object ConfirmView {
  def header(action: ConfirmAction): URIO[Has[ConfirmView], String] = ZIO.serviceWith[ConfirmView](_.header(action))

  val content: URIO[Has[ConfirmView], String] = ZIO.serviceWith[ConfirmView](_.content)

  def footer(message: ConfirmFooterMessage): URIO[Has[ConfirmView], String] =
    ZIO.serviceWith[ConfirmView](_.footer(message))
}
