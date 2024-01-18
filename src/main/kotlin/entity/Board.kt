package entity

import kotlinx.serialization.Serializable

/**
 * class holding the state of indigos board including
 * the games hexagon grid as well as the boards exit gates
 */
@Serializable
data class Board(
    val gates: Array<Pair<PlayerToken, PlayerToken>>,
    val grid: TileGrid
) {
    /** create a deepCopy of the current [Board] instance */
    fun deepCopy(): Board
        = Board(gates.map { it.copy() }.toTypedArray(), grid.deepCopy())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (!gates.contentEquals(other.gates)) return false
        if (grid != other.grid) return false

        return true
    }
}
