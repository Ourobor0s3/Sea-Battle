package Players

import Entities.DataClass.Coordinate
import Entities.DataClass.Ship
import Entities.Enum.StatusCell
import Entities.Enum.TypeOrientation
import Entities.Model.GameBoard
import Entities.Model.Player

class PlayerComputer(gameBoard: GameBoard) : Player(gameBoard) {
    private var lastHit: Coordinate? = null  // Координаты последнего попадания
    private var targetCoordinates: MutableList<Coordinate> = mutableListOf()  // Клетки для дальнейших выстрелов

    override fun makeMove(opponentBoard: GameBoard): Coordinate {
        // Если есть приоритетные цели (клетки для стрельбы), стреляем туда
        if (targetCoordinates.isNotEmpty()) {
            val target = targetCoordinates.removeAt(0)
            if (opponentBoard.isValidMove(target)) {
                return target
            }
        }

        // Если нет целей, выбираем случайную клетку
        var move: Coordinate
        do {
            move = randomMove(opponentBoard.size)
        } while (!opponentBoard.isValidMove(move))

        return move
    }

    // Генерация случайных координат
    private fun randomMove(boardSize: Int): Coordinate {
        val x = (0 until boardSize).random()
        val y = (0 until boardSize).random()
        return Coordinate(x, y)
    }

    override fun processShotResult(opponentBoard: GameBoard, shotCoordinate: Coordinate, result: StatusCell) {
        when (result) {
            StatusCell.HIT -> {
                println("Компьютер попал в корабль на координатах (${shotCoordinate.x}, ${shotCoordinate.y})!")

                if (opponentBoard.isShipSunk(shotCoordinate)) {
                    println("Компьютер потопил корабль!")
                    val ship = opponentBoard.findShipByCoordinate(shotCoordinate)
                    if (ship != null) {
                        opponentBoard.markSurroundingCellsAsMiss(ship)
                    }
                    lastHit = null
                    targetCoordinates.clear()
                } else {
                    lastHit = shotCoordinate
                    targetCoordinates.addAll(generateAdjacentCoordinates(shotCoordinate, opponentBoard.size))
                }
            }
            StatusCell.MISS -> {
                println("Компьютер промахнулся на координатах (${shotCoordinate.x}, ${shotCoordinate.y}).")
            }
            else -> {}
        }
    }

    // Генерация координат вокруг последнего попадания
    private fun generateAdjacentCoordinates(coordinate: Coordinate, boardSize: Int): List<Coordinate> {
        val adjacentCoordinates = mutableListOf<Coordinate>()

        val directions = listOf(
            Coordinate(1, 0),  // Вверх
            Coordinate(-1, 0), // Вниз
            Coordinate(0, 1),  // Вправо
            Coordinate(0, -1)  // Влево
        )

        for (dir in directions) {
            val newX = coordinate.x + dir.x
            val newY = coordinate.y + dir.y
            if (newX in 0 until boardSize && newY in 0 until boardSize) {
                adjacentCoordinates.add(Coordinate(newX, newY))
            }
        }

        return adjacentCoordinates
    }

    override fun placeShips() {
        val shipSizes = listOf(4, 3, 2)
        for (size in shipSizes) {
            var placed = false
            while (!placed) {
                val x = (0 until gameBoard.size).random()
                val y = (0 until gameBoard.size).random()
                val typeOrientation = if ((0..1).random() == 0) TypeOrientation.HORIZONTAL else TypeOrientation.VERTICAL
                val coordinates = generateCoordinates(size, Coordinate(x, y), typeOrientation)
                val ship = Ship(size, coordinates, typeOrientation)
                placed = gameBoard.placeShip(ship)
            }
        }
    }

    private fun generateCoordinates(size: Int, start: Coordinate, typeOrientation: TypeOrientation): List<Coordinate> {
        return (0 until size).map {
            if (typeOrientation == TypeOrientation.HORIZONTAL) {
                Coordinate(start.x, start.y + it)
            } else {
                Coordinate(start.x + it, start.y)
            }
        }
    }
}


