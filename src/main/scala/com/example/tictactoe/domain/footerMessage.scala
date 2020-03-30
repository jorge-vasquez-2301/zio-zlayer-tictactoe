package com.example.tictactoe.domain

sealed trait ConfirmFooterMessage
object ConfirmFooterMessage {
  final case object Empty          extends ConfirmFooterMessage
  final case object InvalidCommand extends ConfirmFooterMessage
}

sealed trait GameFooterMessage
object GameFooterMessage {
  final case object Empty          extends GameFooterMessage
  final case object InvalidCommand extends GameFooterMessage
  final case object FieldOccupied  extends GameFooterMessage
}

sealed trait MenuFooterMessage
object MenuFooterMessage {
  final case object Empty          extends MenuFooterMessage
  final case object InvalidCommand extends MenuFooterMessage
}
