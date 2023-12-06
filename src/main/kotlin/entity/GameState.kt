package entity

/**
 * class holding the entire state of an indigo session
 */
data class GameState(
    val currentPlayer: Player,
    val board: Board,
    val players: List<Player>,
    val drawPile: List<Tile>,
    val previousState: GameState? = null,
    val nextState: GameState? = null
)
