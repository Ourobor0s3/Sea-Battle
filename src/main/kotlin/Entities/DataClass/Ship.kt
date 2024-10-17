package Entities.DataClass

import Entities.Enum.TypeOrientation

data class Ship(val size: Int, val coordinates: List<Coordinate>, val typeOrientation: TypeOrientation)