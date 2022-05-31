package com.example.tictactoe.view.menu

import com.example.tictactoe.domain.MenuFooterMessage
import zio._
import zio.macros._

@accessible
trait MenuView {
  def header: UIO[String]
  def content(isSuspended: Boolean): UIO[String]
  def footer(message: MenuFooterMessage): UIO[String]
}
