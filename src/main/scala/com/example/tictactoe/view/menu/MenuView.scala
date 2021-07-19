package com.example.tictactoe.view.menu

import com.example.tictactoe.domain.MenuFooterMessage
import zio.{ Has, UIO, URIO, ZIO }

trait MenuView {
  def header: UIO[String]
  def content(isSuspended: Boolean): UIO[String]
  def footer(message: MenuFooterMessage): UIO[String]
}
object MenuView {
  val header: URIO[Has[MenuView], String]                             = ZIO.serviceWith[MenuView](_.header)
  def content(isSuspended: Boolean): URIO[Has[MenuView], String]      = ZIO.serviceWith[MenuView](_.content(isSuspended))
  def footer(message: MenuFooterMessage): URIO[Has[MenuView], String] = ZIO.serviceWith[MenuView](_.footer(message))
}
