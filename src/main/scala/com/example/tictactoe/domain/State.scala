package com.example.tictactoe.domain

import com.example.tictactoe.domain.Board.Field

sealed trait State
object State {

  final case class Confirm(
    action: ConfirmAction,
    confirmed: State,
    declined: State,
    footerMessage: ConfirmFooterMessage
  ) extends State
  final case class Menu(game: Option[Game], footerMessage: MenuFooterMessage) extends State
  final case class Game(
    board: Map[Field, Piece],
    cross: Player,
    nought: Player,
    turn: Piece,
    result: GameResult,
    footerMessage: GameFooterMessage
  ) extends State
  case object Shutdown extends State

  val initial: State = Menu(None, MenuFooterMessage.Empty)
}
