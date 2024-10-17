package Entities.Model

import Entities.DataClass.Cell
import Entities.DataClass.Coordinate
import Entities.DataClass.Ship
import Entities.Enum.StatusCell

class GameBoard(val size: Int = 10) {
    val grid = Array(size) { Array(size) { Cell(StatusCell.EMPTY) } }
    private val ships = mutableListOf<Ship>()

    fun placeShip(ship: Ship): Boolean {
        if (canPlaceShip(ship)) {
            ships.add(ship)
            for (coordinate in ship.coordinates) {
                grid[coordinate.x][coordinate.y].status = StatusCell.SHIP
            }
            return true
        }
        return false
    }

    fun receiveShot(coordinate: Coordinate): StatusCell {
        val cell = grid[coordinate.x][coordinate.y]
        return if (cell.status == StatusCell.SHIP) {
            cell.status = StatusCell.HIT
            val ship = findShipByCoordinate(coordinate)
            if (ship != null && isShipSunk(ship)) {
                markSurroundingCellsAsMiss(ship)
            }
            StatusCell.HIT
        } else {
            cell.status = StatusCell.MISS
            StatusCell.MISS
        }
    }

    // Проверяем, что корабль можно разместить на доске, и вокруг него нет других кораблей
    private fun canPlaceShip(ship: Ship): Boolean {
        return ship.coordinates.all { coord ->
            coord.x in 0 until size && coord.y in 0 until size && grid[coord.x][coord.y].status == StatusCell.EMPTY
        } && getAdjacentCoordinates(ship.coordinates).all { coord ->
            coord.x in 0 until size && coord.y in 0 until size && grid[coord.x][coord.y].status == StatusCell.EMPTY
        }
    }

    // Метод для получения всех координат вокруг корабля (в радиусе одной клетки)
    private fun getAdjacentCoordinates(coordinates: List<Coordinate>): List<Coordinate> {
        val adjacentCoordinates = mutableListOf<Coordinate>()
        for (coord in coordinates) {
            for (dx in -1..1) {
                for (dy in -1..1) {
                    val adjX = coord.x + dx
                    val adjY = coord.y + dy
                    if (adjX in 0 until size && adjY in 0 until size && !(dx == 0 && dy == 0)) {
                        adjacentCoordinates.add(Coordinate(adjX, adjY))
                    }
                }
            }
        }
        return adjacentCoordinates
    }

    // Найти корабль по координате
    fun findShipByCoordinate(coordinate: Coordinate): Ship? {
        return ships.find { ship -> coordinate in ship.coordinates }
    }

    // Проверка, потоплен ли корабль
    private fun isShipSunk(ship: Ship): Boolean {
        return ship.coordinates.all { coord ->
            grid[coord.x][coord.y].status == StatusCell.HIT
        }
    }

    // Проверка, потоплен ли корабль по координате
    fun isShipSunk(shotCoordinate: Coordinate): Boolean {
        val ship = findShipByCoordinate(shotCoordinate)
        return ship?.coordinates?.all { this.grid[it.x][it.y].status == StatusCell.HIT } ?: false
    }

    // Пометить клетки вокруг уничтоженного корабля как промахи
    fun markSurroundingCellsAsMiss(ship: Ship) {
        val directions = listOf(
            Coordinate(-1, -1), Coordinate(-1, 0), Coordinate(-1, 1),
            Coordinate(0, -1), Coordinate(0, 1),
            Coordinate(1, -1), Coordinate(1, 0), Coordinate(1, 1)
        )

        for (coord in ship.coordinates) {
            for (dir in directions) {
                val newX = coord.x + dir.x
                val newY = coord.y + dir.y
                if (newX in 0 until this.size && newY in 0 until this.size) {
                    val cell = this.grid[newX][newY]
                    if (cell.status == StatusCell.EMPTY) {
                        cell.status = StatusCell.MISS  // Отмечаем, что по этим клеткам нельзя стрелять
                    }
                }
            }
        }
    }

    // Добавляем метод для проверки допустимости хода (чтобы не стрелять в уже обработанные клетки)
    fun isValidMove(coordinate: Coordinate): Boolean {
        val cell = this.grid[coordinate.x][coordinate.y]
        return cell.status != StatusCell.HIT && cell.status != StatusCell.MISS
    }
}
