package service
import entity.*

/**
 * Class for all the actions a [Player] can take on their turn.
 *
 * @param rootService the [RootService] connects the view with the service layer and the entity layer
 */
class PlayerActionService( private val rootService: RootService) : AbstractRefreshingService() {
    /**
     * place a tile on indigos board and move/award/eliminate gems if applicable
     * @param move pair of the tile to place and the chosen rotation
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

        //check for the illegal moves
        if(isCurveTile(move.first.tileType) && isBlockingTwoGates(position, game.board)){
            throw Exception("It is not permitted to block two gates by putting a curve to both gates")
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

    private fun isCurveTile(tileType: TileType): Boolean {
        return tileType.toType() in listOf(2,3,4)
    }

    private fun isBlockingTwoGates(position: Pair<Int,Int>, board: Board): Boolean {
        return TODO("Implement logic to check if placing the tile at the given position blocks two gates")
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


    fun moveGemToEnd(fromTile: Tile, fromEdge: Int, gem: Gem) {
        //check bordering gates, give points to the owners of the gates
        val gates = getBorderingGates(fromTile, fromEdge)
        if (gates != null) {
            getPlayers().forEach { player: Player ->
                if (player.playerToken == gates.first || player.playerToken == gates.second) {
                    player.collectedGems.add(gem)
                }
            }
            //remove the gem
            fromTile.gems[fromEdge] = null
            return
        }

        val neighbours = getNeighboursOf(fromTile)

        neighbours[fromEdge]?.let { neighbourTile ->
            // Check for collision of gems
            if (neighbourTile.gems[(fromEdge + 3) % 6] != null) {
                fromTile.gems[fromEdge] = null
                neighbourTile.gems[(fromEdge + 3) % 6] = null
                return
            }

            // Find the new edge for the gem to move to
            val newEdge = neighbourTile.paths[(fromEdge + 3) % 6]
            if (newEdge == null) {
                throw IllegalStateException("Path leads to a tile with no connecting path")
            } else {
                // Move the gem one step and repeat
                neighbourTile.gems[newEdge] = gem.ordinal
                moveGemToEnd(neighbourTile, newEdge, gem)
            }
        }
    }

    private fun getBorderingGates(fromTile: Tile, fromEdge: Int): Pair<PlayerToken, PlayerToken>? {
        //Implement logic to determine if the specified edge of the tile is bordering any gates
        return TODO("Implement getBorderingGates")
    }

    /**
     * Get an array of tiles neighbouring [fromTile]. The grid position is calculated by comparing
     * grid entries using reference equality. Passing in copies of the target tile will not work.
     * @param fromTile Reference to an object held in the grid to get the neighbours of.
     * @return An array holding 6 elements which may be null, if there is no neighbour at the associated edge.
     * @throws IllegalStateException when [fromTile] is not in the grid.
     */
    private fun getNeighboursOf(fromTile: Tile): Array<Tile?> {
        val state = checkNotNull(rootService.currentGame)
        val grid = state.board.grid.grid

        val pos = checkNotNull(grid.keys.find {
            grid[it] === fromTile
        }) { "fromTile is not part of the grid" }

        val qPos = pos.first
        val rPos = pos.second

        val offsets = arrayOf(
            Pair(0, -1), Pair(1, -1), Pair(1, 0),
            Pair(0, 1), Pair(-1, 1), Pair(-1, 0)
        )

        return offsets.map {
            val qNei = qPos + it.first
            val rNei = rPos + it.second
            val sNei = -qNei - rNei

            val distanceToCenter = listOf(qNei, rNei, sNei).map { n -> kotlin.math.abs(n) }.max()
            val neighbour = grid.get(Pair(qNei, rNei))

            if (distanceToCenter <= 4) {
                neighbour
            } else {
                null
            }
        }.toTypedArray()
    }

    private fun getPlayers(): List<Player> {
        val game = rootService.currentGame ?: throw IllegalStateException("Game not initialized")
        return game.players
    }


}
