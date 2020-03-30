package com.example.tictactoe.domain

sealed trait Piece
object Piece {
  final case object Cross  extends Piece
  final case object Nought extends Piece
}
