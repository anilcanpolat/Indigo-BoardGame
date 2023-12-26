package entity

import kotlinx.serialization.Serializable

/**
 * class wrapping the state of the games board
 */
@Serializable
data class TileGrid(val grid: HashMap<Pair<Int, Int>, Tile>)
