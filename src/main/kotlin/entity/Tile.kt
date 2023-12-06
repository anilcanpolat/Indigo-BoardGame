package entity

/**
 * class representing a single tile including its paths,
 * its rotation and the gems on top of it
 */
data class Tile(
    var rotation: Int = 0,
    val paths: Array<Int?> = Array(6){null},
    val gems: Array<Int?> = Array(6){null}
) {
    /**
     * construct a new [Tile] object with the paths
     * already set to the correct initial
     * value specified by the given [TileType]
     * @param tileType type of the tile
     */
    constructor(tileType: TileType) : this() {
        val p = pathsForTileType(tileType)

        for (i in 0..5) {
            paths[i] = p[i]
        }
    }
}

private fun pathsForTileType(tileType: TileType): Array<Int?> {
    val type = tileType.toType()

    return if (type != null) {
        val paths: Array<Array<Int?>> = arrayOf(
            arrayOf(2, 4, 0, 5, 1, 3),
            arrayOf(3, 4, 5, 0, 1, 2),
            arrayOf(5, 4, 3, 2, 1, 0),
            arrayOf(5, 3, 4, 1, 2, 0),
            arrayOf(5, 2, 1, 4, 3, 0)
        )

        paths[type]
    } else {
        when (tileType) {
            TileType.TREASURE_CENTER -> arrayOfNulls(6)
            TileType.TREASURE_CORNER -> arrayOf(4, null, null, null, 0, null, null)
            else -> error("only TREASURE_CENTER and TREASURE_CORNER have a type of null")
        }
    }
}
