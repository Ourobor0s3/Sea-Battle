package Entities.Model

import Entities.DataClass.Coordinate
import Entities.Enum.StatusCell

abstract class ModelPlayer(val gameBoard: GameBoard) {
    abstract fun makeMove(opponentBoard: GameBoard): Coordinate

    abstract fun processShotResult(opponentBoard: GameBoard, shotCoordinate: Coordinate, result: StatusCell)
    abstract fun placeShips()
}