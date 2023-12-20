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

    fun playerMove(move: Pair<Tile, Int>, position: Pair<Int, Int>){
        val game = rootService.currentGame
        checkNotNull(game)
        /**if there is already a tile in this position
         * we cancel the placement of this tile in this position
         * and the player should choose another position to place the tile
         */
        if (game.board.grid.grid.get(position) != null) {
            throw Exception("there is already a tile in this position.Choose an other position to place the tile")
        }
        rotate(move.first,move.second)
        game.board.grid.grid.put(position, move.first)
        // for each neighbour of this tile,
        //if there is a gem we move it to the end of the path
        //moveGemToEnd(game.currentPlayer.currentTile)
        game.currentPlayer.currentTile = null
        if (game.drawPile.isEmpty()) {
            endGame()
        } else {
            game.currentPlayer.currentTile = game.drawPile.removeLast()
        }
    }
    private fun rotate(tile:Tile,int:Int){
        //Increment the rotation of the tile by the specified number of steps(int)
        tile.rotation = (tile.rotation + int) % 6
    }

    private fun endGame(){
            val game = rootService.currentGame
            checkNotNull(game) { "Noch kein Spiel gestartet." }
            onAllRefreshables { onGameFinished(game.players) }
        }



    /**
     * Move a gem to the end of the path starting at fromTile at the
     * edge specified by fromEdge.
     * @param fromTile tile the gem is currently placed on
     * @param fromEdge number of the edge the gem currently lays on
     * @param gem the gem to move
     */

    /**
    fun moveGemToEnd(fromTile: Tile, fromEdge: Int, gem: Gem) {
        //check bordering gates, give points to the owners of the gates
        val gates = getBorderingGates(fromTile, fromEdge)
        if(gates != null){
            getPlayers().forEach { player ->
                if (player.playerToken == gates.first || player.playerToken == gates.second) {
                    player.collectedGems.add(gem)
                }
            }
            //remove the gem
            fromTile.gems[fromEdge] = null
            return
        }

        val neighbours = getNeighboursOf(fromTile)

        //no neighbour means the gem is already at the end of the path
        if(neighbours[fromEdge] == null) {
            return
        }

        //Gem on the same route,colliding.Remove gems.
        if(neighbours[fromEdge]?.gems[(fromEdge + 3) % 6] != null) {
            fromTile.gems[fromEdge] = null
            neighbours[fromEdge]?.gems[(fromEdge + 3) % 6] = null
            return
        }

        val newEdge = neighbours[fromEdge]?.paths[(fromEdge + 3) % 6] ?: throw IllegalStateException("Path leads to a tile with no connecting path")

        // Move gem one step. Repeat the gem moving through recursion
        neighbours[fromEdge]?.gems[newEdge] = gem
        moveGemToEnd(neighbours[fromEdge]!!, newEdge, gem)
    }
    */
}
