package entity

/**
 * class wrapping the state of the games board
 */
data class TileGrid(val grid: HashMap<Pair<Int, Int>, Tile>)
