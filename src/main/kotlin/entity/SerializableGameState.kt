package entity

import kotlinx.serialization.Serializable

/**
 * Data structure used while serializing and deserializing the [GameState].
 * All references which could result in dependency cycles have been removed.
 * All other fields sever exactly the same purpose as in [GameState].
 */
@Serializable
data class SerializableGameState(
    val currentPlayer: Player,
    val board: Board,
    val players: List<Player>,
    val drawPile: MutableList<Tile>
) {
    /**
     * Create a [SerializableGameState] from a normal [GameState] instance,
     * discarding the [GameState.previousState] and [GameState.nextState] references.
     */
    constructor(state: GameState) : this(state.currentPlayer, state.board, state.players, state.drawPile)
}