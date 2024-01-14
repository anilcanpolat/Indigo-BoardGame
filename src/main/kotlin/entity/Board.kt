package entity

/**
 * class holding the state of indigos board including
 * the games hexagon grid as well as the boards exit gates
 */
data class Board(
    val gates: Array<Pair<PlayerToken, PlayerToken>>,
    val grid: TileGrid
) {
    /** create a deepCopy of the current [Board] instance */
    fun deepCopy(): Board
        = Board(gates.map { it.copy() }.toTypedArray(), grid.deepCopy())
}
