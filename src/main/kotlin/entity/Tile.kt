package entity

/**
 * class representing a single tile including its paths,
 * its rotation and the gems on top of it
 */
data class Tile(
    val tileType: TileType,
    var rotation: Int = 0,
    val paths: Array<Int?> = Array(6){null},
    val gems: Array<Gem?> = Array(6){null}
) {
    /**
     * construct a new [Tile] object with the paths
     * already set to the correct initial
     * value specified by the given [TileType]
     * @param tileType type of the tile
     */
    constructor(tileType: TileType) : this(tileType, 0) {
        val p = pathsForTileType(tileType)

        for (i in 0..5) {
            paths[i] = p[i]
        }

        val g = gemsForTileType(tileType)

        for (i in 0..5) {
            gems[i] = g[i]
        }
    }

    /** create a deepCopy of the current [Tile] instance */
    fun deepCopy(): Tile = Tile(tileType, rotation, paths.copyOf(), gems.copyOf())

    /** rotate the tile by [value] steps clockwise */
    fun rotate(value: Int) {
        rotation = (rotation + value) % 6

        for (i in 0..value) {
            val lstGem = gems[5]
            val lstPth = paths[5]

            for (j in 0..4) {
                gems[j + 1] = gems[j]
                paths[j + 1] = paths[j]
            }

            gems[0] = lstGem
            paths[0] = lstPth

            for (j in 0..5) {
                paths[j] = paths[j]?.inc()
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Tile) {
            return false
        }

        val tile: Tile = other

        if (this.tileType != tile.tileType || this.rotation != tile.rotation) {
            return false
        }

        if (!this.gems.contentEquals(tile.gems) || !this.paths.contentEquals(tile.paths)) {
            return false
        }

        return true
    }

    override fun hashCode(): Int = tileType.hashCode() * rotation.hashCode()
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

private fun gemsForTileType(tileType: TileType): Array<Gem?> =
    when(tileType) {
        TileType.TREASURE_CENTER -> Array(6) {
            if (it >= 5) {
                Gem.SAPHIRE
            } else {
                Gem.EMERALD
            }
        }

        TileType.TREASURE_CORNER -> Array(6) {
            if (it == 3) {
                Gem.AMBER
            } else {
                null
            }
        }

        else -> Array(6) { null }
    }
