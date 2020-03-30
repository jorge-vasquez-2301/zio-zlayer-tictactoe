package com.example.tictactoe.domain

sealed trait Player
object Player {
  final case object Ai    extends Player
  final case object Human extends Player
}
