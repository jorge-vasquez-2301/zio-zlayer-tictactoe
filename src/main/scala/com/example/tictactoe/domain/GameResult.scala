package com.example.tictactoe.domain

sealed trait GameResult
object GameResult {
  final case object Ongoing          extends GameResult
  final case class Win(piece: Piece) extends GameResult
  final case object Draw             extends GameResult
}
