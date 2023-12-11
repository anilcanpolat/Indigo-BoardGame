package entity

/**
 * Enumeration over all kinds of tiles present in indigo, including the
 * treasure tiles at the center and border of the games board.
 */
enum class TileType {
    CORNERS_ONLY,
    STRAIGHT_NOCROSS,
    CURVES_TO_CORNER,
    STRAIGHTS_ONLY,
    LONG_CURVES,
    TREASURE_CORNER,
    TREASURE_CENTER;

    /**
     * Get the type id used in the .csv file found under
     * https://sopra.cs.tu-dortmund.de/wiki/_media/sopra/23d/tiles.csv
     * for the given variant. The variants [TREASURE_CORNER] and [TREASURE_CENTER]
     * do not have such an id, so the results will be null.
     * @return integer value between 0 and 4 or null if the variant is [TREASURE_CENTER] or [TREASURE_CORNER]
     */
    fun toType(): Int? = when(this) {
        CORNERS_ONLY -> 4
        STRAIGHT_NOCROSS -> 2
        STRAIGHTS_ONLY -> 1
        LONG_CURVES -> 0
        CURVES_TO_CORNER -> 3
        else -> null
    }
}