package entity

import kotlinx.serialization.Serializable

/**
 * class wrapping the state of the games board
 */
@Serializable
data class TileGrid(val grid: HashMap<Pair<Int, Int>, Tile>) {
    /** create a deepCopy of the current [TileGrid] instance */
    fun deepCopy(): TileGrid {
        val newMap: HashMap<Pair<Int, Int>, Tile> = hashMapOf()

        grid.forEach { (k, v) ->
            newMap[k.copy()] = v.deepCopy()
        }

        return TileGrid(newMap)
    }
}
