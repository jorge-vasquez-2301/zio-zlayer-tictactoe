package com.example.tictactoe.domain

sealed trait ConfirmAction
object ConfirmAction {
  final case object NewGame extends ConfirmAction
  final case object Quit    extends ConfirmAction
}
