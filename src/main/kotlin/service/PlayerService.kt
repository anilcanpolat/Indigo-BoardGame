package service
import entity.*
/**
 * Class for all the actions a [IndigoPlayer] can take on their turn.
 *
 * @param rootService the [RootService] connects the view with the service layer and the entity layer
 */
class PlayerService( val rootService: RootService) : AbstractRefreshingService() {
    /**
     * place a tile on indigos board and move/award/eliminate gems if applicable
     * @param player the current player placing the tile
     * @param rotation rotation the player chose for the tile
     * @param position position where the tile will be placed
     */

    fun playerMove(move: Pair<Tile, Int>, position: Pair<Int, Int>){
        /*val game = rootService.currentGame
        checkNotNull(game)
        /**if there is already a tile in this position
         * we cancel the placement of this tile in this position
         * and the player should choose another position to place the tile
         */
        if (game.board.grid.grid.get(position) != null) {
           // cancelTilePlacement()
            return
        }
        val tile = player.currentTile
        tile.rotate(rotation)
        game.board.grid.grid.put(position, tile)
        game.board.grid.grid.put(position, tile)
        player.currentTile = null
        // for each neighbour of this tile,
        //if there is a gem we move it to the end of the path
        if (game.drawPile.isEmpty()) {
            endGame()
        } else {
            player.currentTile = game.drawPile.removeLast()
        }*/
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
