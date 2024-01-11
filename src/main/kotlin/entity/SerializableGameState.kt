package entity

import kotlinx.serialization.Serializable

@Serializable
data class SerializableGameState(
    val currentPlayer: Player,
    val board: Board,
    val players: List<Player>,
    val drawPile: MutableList<Tile>,
) {
    constructor(state: GameState) : this(state.currentPlayer, state.board, state.players, state.drawPile)
}