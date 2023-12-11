package entity

/**
 * Enumeration over gem colors present in the game.
 */
enum class Gem {
    SAPHIRE,
    EMERALD,
    AMBER;

    /**
     * Calculate the points a player would receive by receiving this gem.
     * @return integer value between 1 and 3 (inclusive)
     */
    fun score(): Int {
        if (this == SAPHIRE) {
            return 3
        } else if (this == EMERALD) {
            return 2
        } else {
            return 1
        }
    }
}