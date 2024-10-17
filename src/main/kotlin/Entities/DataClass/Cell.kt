package Entities.DataClass

import Entities.Enum.StatusCell

data class Cell(var status: StatusCell) {
    fun getStat(showShips: Boolean = true): String {
        return when (status) {
            StatusCell.EMPTY -> "~"  // Пустая клетка
            StatusCell.SHIP -> if (showShips) "\uD83C\uDD42" else "~"  // Корабль, показывает в случае если showShips истина
            StatusCell.HIT -> "X"    // Попадание
            StatusCell.MISS -> "◯"   // Промах
        }
    }
}