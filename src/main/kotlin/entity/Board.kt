package entity

import kotlinx.serialization.Serializable

/**
 * class holding the state of indigos board including
 * the games hexagon grid as well as the boards exit gates
 */
@Serializable
data class Board(
    val gates: Array<Pair<PlayerToken, PlayerToken>>,
    val grid: TileGrid,
)
