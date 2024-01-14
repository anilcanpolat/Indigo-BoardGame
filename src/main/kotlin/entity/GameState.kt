package entity

/**
 * class holding the entire state of an indigo session
 */
data class GameState(
    var currentPlayer: Player,
    val board: Board,
    val players: List<Player>,
    val drawPile: MutableList<Tile>,
    var previousState: GameState? = null,
    var nextState: GameState? = null
) {
    /**
     * Create a deepCopy of the current [GameState] instance.
     * The [currentPlayer] field will reference one of the entries
     * of [players], it will not be deepCopied itself. The [previousState]
     * and [nextState] fields will also not be copied, both will remain as is.
     */
    fun deepCopy(): GameState {
        var previousPlayerIndex = 0

        for (player in players) {
            if (player === currentPlayer) {
                break
            }

            previousPlayerIndex += 1
        }

        val listCopy = players.map { it.deepCopy() }

        return GameState(
            listCopy[previousPlayerIndex],
            board.deepCopy(),
            listCopy,
            drawPile.map { it.deepCopy() }.toMutableList(),
            previousState,
            nextState
        )
    }
}