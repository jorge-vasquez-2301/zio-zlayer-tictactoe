package com.example.tictactoe.domain

import com.example.tictactoe.domain.Board.Field

sealed trait ConfirmCommand
object ConfirmCommand {
  final case object Yes extends ConfirmCommand
  final case object No  extends ConfirmCommand
}

sealed trait GameCommand
object GameCommand {
  final case object Menu             extends GameCommand
  final case class Put(field: Field) extends GameCommand
}

sealed trait MenuCommand
object MenuCommand {
  final case object NewGame extends MenuCommand
  final case object Resume  extends MenuCommand
  final case object Quit    extends MenuCommand
}
