package com.example.tictactoe.terminal

import zio._

import zio.macros._

@accessible
trait Terminal {
  def getUserInput: UIO[String]
  def display(frame: String): UIO[Unit]
}
