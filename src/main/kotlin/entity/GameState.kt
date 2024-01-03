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
)
