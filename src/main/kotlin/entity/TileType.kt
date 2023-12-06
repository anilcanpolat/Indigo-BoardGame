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
    TREASURE_CENTER,
}