package entity

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * class holding the entire state of an indigo session
 */
@Serializable
data class GameState(
    @Contextual val currentPlayer: Player,
    @Contextual val board: Board,
    @Contextual val players: List<Player>,
    @Contextual val drawPile: MutableList<Tile>,
    @Contextual var previousState: GameState? = null,
    @Contextual var nextState: GameState? = null
)
