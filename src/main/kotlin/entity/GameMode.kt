package entity

/**
 * enumeration over all supported game modes
 */
enum class GameMode {
    TWO_PLAYERS,
    THREE_PLAYERS,
    THREE_PLAYERS_SHARED_GATES,
    FOUR_PLAYERS;

    /**
     * Get the gate configuration associated with the [GameMode].
     * The gate configuration is fully specified by the games
     * rules on page14, it does not depend on the order players
     * take turns.
     */
    fun gateConfiguration(): Array<Pair<PlayerToken, PlayerToken>> {
        val white = PlayerToken.WHITE
        val red = PlayerToken.RED
        val purple = PlayerToken.PURPLE
        val cyan = PlayerToken.CYAN

        val redPair = Pair(red, red)
        val cyanPair = Pair(cyan, cyan)
        val whitePair = Pair(white, white)

        val config = arrayOf(
            arrayOf(redPair, cyanPair, redPair, cyanPair, redPair, cyanPair),
            arrayOf(redPair, cyanPair, whitePair, redPair, cyanPair, whitePair),
            arrayOf(redPair, Pair(red, cyan), whitePair, Pair(white, red), cyanPair, Pair(cyan, white)),
            arrayOf(
                Pair(red, cyan), Pair(cyan, white),
                Pair(red, purple), Pair(purple, cyan),
                Pair(white, red), Pair(white, purple)
            )
        )

        return config[this.ordinal]
    }
}
