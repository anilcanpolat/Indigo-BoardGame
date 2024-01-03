package service
import entity.*
/**
 * Class for all the actions a [IndigoPlayer] can take on their turn.
 *
 * @param rootService the [RootService] connects the view with the service layer and the entity layer
 */
class PlayerActionService( val rootService: RootService) : AbstractRefreshingService() {
    /**
     * place a tile on indigos board and move/award/eliminate gems if applicable
     * @param player the current player placing the tile
     * @param rotation rotation the player chose for the tile
     * @param position position where the tile will be placed
     */

    fun playerMove(move: Pair<Tile, Int>, position: Pair<Int, Int>) {
        /* Execute an appropriate refreshable call for testing purposes. This
           should be replaced with an actual implementation when merging. */

        val state = checkNotNull(rootService.currentGame)
        val currentPlayerIndex = state.players.indexOf(state.currentPlayer)
        val nextPlayer = state.players[(currentPlayerIndex + 1) % state.players.size]

        val currentPlayer = state.currentPlayer
        state.currentPlayer = nextPlayer

        onAllRefreshables { onPlayerMove(currentPlayer, nextPlayer, move.first, position, move.second) }
    }
    /**
     * Move a gem to the end of the path starting at fromTile at the
     * edge specified by fromEdge.
     * @param fromTile tile the gem is currently placed on
     * @param fromEdge number of the edge the gem currently lays on
     * @param gem the gem to move
     */
    fun moveGemToEnd(fromTile: Tile, fromEdge: Int, gem: Gem) {}

}
