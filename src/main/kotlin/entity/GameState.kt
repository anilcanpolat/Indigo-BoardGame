package entity

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * class holding the entire state of an indigo session
 */
@Serializable
data class GameState(
    val currentPlayer: Player,
     val board: Board,
     val players: List<Player>,
     val drawPile: MutableList<Tile>,
     var previousState: GameState? = null,
     var nextState: GameState? = null
)//@Contextual
