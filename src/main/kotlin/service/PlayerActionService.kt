package service

import java.lang.Thread
import entity.*

/**
 * Class for all the actions a [Player] can take on their turn.
 *
 * @param rootService the [RootService] connects the view with the service layer and the entity layer
 */
class PlayerActionService(private val rootService: RootService) : AbstractRefreshingService() {
    /**
     * place a tile on indigos board and move/award/eliminate gems if applicable
     * @param move pair of the tile to place and the chosen rotation
     * @param position position where the tile will be placed
     */

    fun playerMove(move: Pair<Tile, Int>, position: Pair<Int, Int>) {
        var game = checkNotNull(rootService.currentGame) { "game is not initialized" }

        check(!gameIsFinished()) { "cannot perform any moves in a finished game" }

        check(CommonMethods.distanceToCenter(position) <= 4) { "invalid position" }

        // prevent double placement of tiles
        check(game.board.grid.grid[position] == null) { "attempted to place a tile at an occupied position" }

        //check for the illegal moves
        check(!isBlockingGates(move.first, move.second, position)) {
            "It is not permitted to block two gates by putting a curve to both gates"
        }

        val previousGame = checkNotNull(rootService.currentGame)
        rootService.currentGame = rootService.currentGame?.deepCopy()

        previousGame.nextState = rootService.currentGame
        rootService.currentGame?.previousState = previousGame
        rootService.currentGame?.nextState = null

        game = checkNotNull(rootService.currentGame)

        move.first.rotate(move.second)
        game.board.grid.grid[position] = move.first

        val neighbours = getNeighboursOf(move.first)
        val isAI = game.currentPlayer.playerType == PlayerType.COMPUTER
        val isRemote = game.currentPlayer.playerType == PlayerType.REMOTE
        var count =0
        for (i in 0..5) {
            val currentNeighbour = neighbours[i] ?: continue
            val connectingEdge = (i + 3) % 6

            if (currentNeighbour.tileType == TileType.TREASURE_CENTER) {
                val emeraldIndex = currentNeighbour.gems.indexOf(Gem.EMERALD)
                var gem = currentNeighbour.gems[connectingEdge]

                if (emeraldIndex != -1 && currentNeighbour.gems[connectingEdge] != null ) {
                    currentNeighbour.gems[emeraldIndex] = currentNeighbour.gems[connectingEdge].also {
                        currentNeighbour.gems[connectingEdge] = currentNeighbour.gems[emeraldIndex]
                    }
                    gem = Gem.EMERALD
                } else if (count ==5)  {
                    val saphireIndex = currentNeighbour.gems.indexOf(Gem.SAPHIRE)
                    check(saphireIndex >= 0) { "more than 6 gems removed from center tile" }

                    // swap the saphire with the gem at the current position. Positions on the center
                    // tile are irrelevant, but [moveGemToEnd] requires them to be set correctly.
                    currentNeighbour.gems[saphireIndex] = currentNeighbour.gems[connectingEdge].also {
                        currentNeighbour.gems[connectingEdge] = currentNeighbour.gems[saphireIndex]
                    }
                    Gem.SAPHIRE
                }
                if (gem != null) {
                    count +=1
                    val path = moveGemToEnd(currentNeighbour, connectingEdge, gem)
                    onAllRefreshables { onGemMove(path) }
                }
            } else {
                val gem = currentNeighbour.gems[connectingEdge]
                if (gem != null) {
                    val path = moveGemToEnd(currentNeighbour, connectingEdge, gem)
                    onAllRefreshables { onGemMove(path) }
                }
            }
        }

        val refreshList: MutableList<() -> Unit> = mutableListOf()

        val nextPlayerIndex = (game.players.indexOf(game.currentPlayer) + 1) % game.players.size
        val nextPlayer = game.players[nextPlayerIndex]
        val currentPlayer = game.currentPlayer

        if (!isRemote && isNetworkGame()) {
            rootService.networkService.sendTilePlaced(move.second, position)
        }

        refreshList.add {
            onAllRefreshables { onPlayerMove(currentPlayer, nextPlayer, move.first, position, move.second) }
        }

        game.currentPlayer.currentTile = null

        if (game.drawPile.isNotEmpty()) {
            game.currentPlayer.currentTile = game.drawPile.removeLast()
        }

        game.currentPlayer = nextPlayer

        if (gameIsFinished()) {
            refreshList.add {
                onAllRefreshables {
                    onGameFinished(game.players)
                }
            }
        }

        // run refreshes only after the state has been fully updated
        for (refresh in refreshList) {
            refresh()
        }

        if (!isAI) {
            processAllAIMoves()
        }

        if (!isAI && !isRemote) {
            onAllRefreshables { onWaitForInput() }
        }
    }

    private fun isNetworkGame(): Boolean =
        checkNotNull(rootService.currentGame).players.any {
            it.playerType == PlayerType.REMOTE
        }

    /**
     * Execute moves calculated by AI until the current player is no longer of type [PlayerType.COMPUTER]
     */
    fun processAllAIMoves() {
        while (!gameIsFinished()) {
            val state = checkNotNull(rootService.currentGame) { "game state not initialized" }
            val player = state.currentPlayer

            if (player.playerType != PlayerType.COMPUTER) {
                break
            }

            val move: Pair<Pair<Tile, Int>, Pair<Int, Int>> = if (player.useRandomAI) {
                AIService(rootService).randomMove()
            } else {
                AIService(rootService).properMoveForAI()
            }

            Thread.sleep(player.aiDelay.toLong())
            playerMove(move.first, move.second)

        }
    }

    /**
     * Move a gem to the end of the path starting at fromTile at the
     * edge specified by fromEdge.
     * @param fromTile tile the gem is currently placed on
     * @param fromEdge number of the edge the gem currently lays on
     * @param gem the gem to move
     * @return a list of all positions and edges the gem was on
     */
    private fun moveGemToEnd(fromTile: Tile, fromEdge: Int, gem: Gem): List<Pair<Pair<Int, Int>, Int>> {
        //check bordering gates, give points to the owners of the gates
        val gates = getBorderingGates(fromTile, fromEdge)
        val currentPosition = checkNotNull(getTilePosition(fromTile)) { "fromTile not placed on the grid" }

        if (gates != null) {
            getPlayers().forEach { player: Player ->
                if (player.playerToken == gates.first || player.playerToken == gates.second) {
                    player.collectedGems.add(gem)
                }
            }

            fromTile.gems[fromEdge] = null
            onAllRefreshables { onGemRemoved(currentPosition, fromEdge) }

            return listOf(Pair(currentPosition, fromEdge))
        }

        val neighbours = getNeighboursOf(fromTile)
        val neighbourTile = neighbours[fromEdge]
        if (neighbourTile != null) {
            // Check for collision of gems
            if (neighbourTile.gems[(fromEdge + 3) % 6] != null) {
                val neighbourPosition = checkNotNull(getTilePosition(neighbourTile))

                fromTile.gems[fromEdge] = null
                neighbourTile.gems[(fromEdge + 3) % 6] = null

                onAllRefreshables { onGemRemoved(currentPosition, fromEdge) }
                onAllRefreshables { onGemRemoved(neighbourPosition, (fromEdge + 3) % 6) }

                return listOf(Pair(currentPosition, fromEdge))
            }

            // Find the new edge for the gem to move to
            val newEdge = neighbourTile.paths[(fromEdge + 3) % 6]

            if (newEdge == null) {
                throw IllegalStateException("Path leads to a tile with no connecting path")
            } else {
                // Move the gem one step and repeat
                neighbourTile.gems[newEdge] = gem
                fromTile.gems[fromEdge] = null

                return listOf(Pair(currentPosition, fromEdge)) + moveGemToEnd(neighbourTile, newEdge, gem)
            }
        }

        return listOf(Pair(currentPosition, fromEdge))
    }

    /**
     * Test whether the game has finished. A game is considered to be finished if
     * - No more tiles are on the draw pile
     * - There are no more gems on the board
     * - No valid move can be executed by the next player
     */
    private fun gameIsFinished(): Boolean {
        val gameState = checkNotNull(rootService.currentGame).deepCopy()

        val anyGemsOnBoard = gameState.board.grid.grid.values.any {
            it.gems.any { gem -> gem != null }
        }

        if (!anyGemsOnBoard) { return true }
        if (gameState.drawPile.isEmpty()) { return true }

        val player = gameState.currentPlayer
        val tile = player.currentTile ?: gameState.drawPile.removeLast()

        player.currentTile = tile

        val validMoveExists = allCoordinates().any { pos ->
            (0..5).any { rot ->
                CommonMethods.isValidMove(gameState, tile, rot, pos)
            }
        }

        return !validMoveExists
    }

    private fun allCoordinates(): List<Pair<Int, Int>> =
        (-4..4).flatMap { q ->
            (-4..4).map { r ->
                Pair(q, r)
            }
        }.filter {
            CommonMethods.distanceToCenter(it) <= 4
        }


    /**
     * Get the gate bordering a given tiles edge, if any exist.
     * @param fromTile tile holding the possibly bordering edge
     * @param fromEdge edge possibly bordering the gate
     * @return [PlayerToken] pair of the gate owners
     */
    private fun getBorderingGates(fromTile: Tile, fromEdge: Int): Pair<PlayerToken, PlayerToken>? {
        val pos = checkNotNull(getTilePosition(fromTile)) { "fromTile is not stored in the grid" }
        val boardEdge = CommonMethods.borderingGateNumber(pos) ?: return null

        // The edges 1 and 2 connect to gate 1, the edges 2 and 3 to gate 2, 5 and 0 to gate 5 and so on
        return if (fromEdge == boardEdge || fromEdge == (boardEdge + 1) % 6) {
            val gates = checkNotNull(rootService.currentGame).board.gates
            gates[boardEdge]
        } else {
            null
        }
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

        val pos = checkNotNull(getTilePosition(fromTile)) { "fromTile is not part of the grid" }

        return CommonMethods.neighbouringPositions(pos).map {
            val distance = CommonMethods.distanceToCenter(it)
            val neighbour = grid[it]

            if (distance <= 4) {
                neighbour
            } else {
                null
            }
        }.toTypedArray()
    }

    /**
     * Check whether a tile connects two gates directly, leading to a blocked gate.
     * @param fromTile tile which might be blocking a gate. The currently applied rotation is considered.
     * @param rotation amount of rotation applied to [fromTile]. [fromTile] itself will not be altered.
     * @param position position the tile would be placed at
     * @return true if the [fromTile] would block a gate when placed at [position]
     */
    private fun isBlockingGates(fromTile: Tile, rotation: Int, position: Pair<Int, Int>): Boolean {
        val tileCopy = fromTile.deepCopy()
        tileCopy.rotate(rotation)

        val idx = CommonMethods.borderingGateNumber(position) ?: return false
        val edgeB = (idx + 1) % 6

        return tileCopy.paths[idx] == edgeB
    }

    /**
     * Get the grid position where [tile] is stored. The grid position is calculated by comparing
     * grid entries using reference equality. Passing in copies of the target tile will not work.
     * @param tile tile to get the position of
     * @return [Tile] position is axial coordinates or null if the tile was not found.
     */
    private fun getTilePosition(tile: Tile): Pair<Int, Int>? {
        val state = checkNotNull(rootService.currentGame)
        val grid = state.board.grid.grid

        return grid.keys.find {
            grid[it] === tile
        }
    }

    private fun getPlayers(): List<Player> {
        val game = rootService.currentGame ?: throw IllegalStateException("Game not initialized")
        return game.players
    }


}
