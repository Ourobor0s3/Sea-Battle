package Players

import Entities.DataClass.Coordinate
import Entities.DataClass.Ship
import Entities.Enum.CellStatus
import Entities.Enum.Orientation
import Entities.Model.Board
import Entities.Model.Player

class HumanPlayer(board: Board) : Player(board) {

    override fun processShotResult(opponentBoard: Board, shotCoordinate: Coordinate, result: CellStatus) {
        when (result) {
            CellStatus.HIT -> {
                println("Попадание в корабль на координатах (${shotCoordinate.x}, ${shotCoordinate.y})!")
                if (opponentBoard.isShipSunk(shotCoordinate)) {
                    println("Корабль потоплен!")
                    val ship = opponentBoard.findShipByCoordinate(shotCoordinate)
                    if (ship != null) {
                        opponentBoard.markSurroundingCellsAsMiss(ship)
                    }
                }
            }
            CellStatus.MISS -> {
                println("Промах на координатах (${shotCoordinate.x}, ${shotCoordinate.y}).")
            }
            else -> {}
        }
    }

    override fun makeMove(opponentBoard: Board): Coordinate {
        // Запрашиваем координаты у игрока
        println("Введите координаты для выстрела (формат: x y):")
        val input = readLine()?.split(" ") ?: listOf()
        if (input.size == 2) {
            val x = input[0].toIntOrNull()
            val y = input[1].toIntOrNull()
            if (x != null && y != null && x in 0 until opponentBoard.size && y in 0 until opponentBoard.size) {
                return Coordinate(x, y)
            }
        }
        println("Некорректный ввод, попробуйте снова.")
        return makeMove(opponentBoard)  // Повторяем запрос
    }

    override fun placeShips() {
        val shipSizes = listOf(4, 3, 2)
        for (size in shipSizes) {
            var placed = false
            while (!placed) {
                println("Установите корабль длиной $size (введите: x y h/v):")
                val input = readLine()?.split(" ") ?: listOf()
                if (input.size == 3) {
                    val x = input[0].toIntOrNull()
                    val y = input[1].toIntOrNull()
                    val orientationInput = input[2]
                    if (x != null && y != null && x in 0 until board.size && y in 0 until board.size) {
                        val orientation = if (orientationInput == "h") Orientation.HORIZONTAL else Orientation.VERTICAL
                        val coordinates = generateCoordinates(size, Coordinate(x, y), orientation)
                        val ship = Ship(size, coordinates, orientation)
                        placed = board.placeShip(ship)
                        if (placed) {
                            println("Корабль успешно установлен!")
                            printBoard()
                        } else {
                            println("Некорректное размещение корабля! Убедитесь, что вокруг корабля есть зазор в 1 клетку.")
                        }
                    } else {
                        println("Некорректные координаты! Попробуйте снова.")
                    }
                } else {
                    println("Некорректный ввод, попробуйте снова.")
                }
            }
        }
    }

    // Вывод доски игрока с отображением состояний клеток
    private fun printBoard() {
        println("  " + (0 until board.size).joinToString(" ") { it.toString() })
        board.grid.forEachIndexed { rowIndex, row ->
            print("$rowIndex ")
            row.forEach { cell ->
                val symbol = when (cell.status) {
                    CellStatus.EMPTY -> "~"
                    CellStatus.SHIP -> "S"
                    CellStatus.HIT -> "X"
                    CellStatus.MISS -> "O"
                }
                print("$symbol ")
            }
            println()
        }
    }

    private fun generateCoordinates(size: Int, start: Coordinate, orientation: Orientation): List<Coordinate> {
        return (0 until size).map {
            if (orientation == Orientation.HORIZONTAL) {
                Coordinate(start.x, start.y + it)
            } else {
                Coordinate(start.x + it, start.y)
            }
        }
    }
}
