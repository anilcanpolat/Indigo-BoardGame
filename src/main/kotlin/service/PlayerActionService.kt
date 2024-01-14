package service
import entity.*
import java.util.HashSet
import kotlin.math.abs

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

    fun playerMove(move: Pair<Tile, Int>, position: Pair<Int, Int>) {
        val game = checkNotNull(rootService.currentGame)

        // create a copy so that using undo/redo works
        val gameCopy = game.deepCopy()
        gameCopy.nextState = game

        // prevent double placement of tiles
        check(game.board.grid.grid[position] == null) { "attempted to place a tile at an occupied position" }

        //check for the illegal moves
        check(!isBlockingGates(move.first, position)) {
            "It is not permitted to block two gates by putting a curve to both gates"
        }

        game.board.grid.grid[position] = move.first

        val neighbours = getNeighboursOf(move.first)

        for (i in 0..5) {
            val currentNeighbour = neighbours[i] ?: continue
            val connectingEdge = (i + 3) % 6

            if (currentNeighbour.tileType == TileType.TREASURE_CENTER) {
                val emeraldIndex = currentNeighbour.gems.indexOf(Gem.EMERALD)

                val gem = if (emeraldIndex != -1) {
                    // swap the emerald with the gem at the current position. Positions on the center
                    // tile are irrelevant, but [moveGemToEnd] requires them to be set correctly.
                    currentNeighbour.gems[emeraldIndex] = currentNeighbour.gems[connectingEdge].also {
                        currentNeighbour.gems[connectingEdge] = currentNeighbour.gems[emeraldIndex]
                    }

                    Gem.EMERALD
                } else {
                    val saphireIndex = currentNeighbour.gems.indexOf(Gem.SAPHIRE)
                    check(saphireIndex >= 0) { "more than 6 gems removed from center tile" }

                    // swap the saphire with the gem at the current position. Positions on the center
                    // tile are irrelevant, but [moveGemToEnd] requires them to be set correctly.
                    currentNeighbour.gems[saphireIndex] = currentNeighbour.gems[connectingEdge].also {
                        currentNeighbour.gems[connectingEdge] = currentNeighbour.gems[saphireIndex]
                    }

                    Gem.SAPHIRE
                }

                val path = moveGemToEnd(currentNeighbour, connectingEdge, gem)
                onAllRefreshables { onGemMove(path) }
            } else {
                val gem = currentNeighbour.gems[connectingEdge]

                if (gem != null) {
                    val path = moveGemToEnd(currentNeighbour, connectingEdge, gem)
                    onAllRefreshables { onGemMove(path) }
                }
            }
        }

        val nextPlayerIndex = (game.players.indexOf(game.currentPlayer) + 1) % game.players.size
        val nextPlayer = game.players[nextPlayerIndex]

        onAllRefreshables { onPlayerMove(game.currentPlayer, nextPlayer, move.first, position, move.second) }

        game.currentPlayer.currentTile = null

        if (game.drawPile.isNotEmpty()) {
            game.currentPlayer.currentTile = game.drawPile.removeLast()
        } else {
            onAllRefreshables { onGameFinished(game.players) }
        }

        game.currentPlayer = nextPlayer

        game.previousState = gameCopy
        game.nextState = null
        gameCopy.nextState = game
    }

    private fun isCurveTile(tileType: TileType): Boolean {
        return tileType.toType() in listOf(2,3,4)
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

        neighbours[fromEdge]?.let { neighbourTile ->
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
     * Get the gate bordering a given tiles edge, if any exist.
     * @param fromTile tile holding the possibly bordering edge
     * @param fromEdge edge possibly bordering the gate
     * @return [PlayerToken] pair of the gate owners
     */
    private fun getBorderingGates(fromTile: Tile, fromEdge: Int): Pair<PlayerToken, PlayerToken>? {
        val pos = checkNotNull(getTilePosition(fromTile)) { "fromTile is not stored in the grid" }
        val boardEdge = getBorderingGateIndex(pos) ?: return null

        // The edges 1 and 2 connect to gate 1, the edges 2 and 3 to gate 2, 5 and 0 to gate 5 and so on
        return if (fromEdge == boardEdge || fromEdge == (boardEdge + 1) % 6) {
            val gates = checkNotNull(rootService.currentGame).board.gates
            gates[boardEdge]
        } else {
            null
        }
    }

    /**
     * Get the gate a tile might be bordering when placed at [pos], if it exists.
     * The result is an index into the [Board.gates] array.
     * @param pos tile which is possibly bordering a gate
     * @return index into the [Board.gates] array or null if [pos] is not bordering any gates
     */
    private fun getBorderingGateIndex(pos: Pair<Int, Int>): Int? {
        val distance = distanceToCenter(pos)

        // only tiles in the outermost ring border gates
        if (distance != 4) {
            return null
        }

        // treasure tiles do not have bordering gates (or none that have to be considered)
        if (isTreasureTilePosition(pos)) {
            return null
        }

        // only tiles bordering a gate remain
        val border = getOuterPositions()
        val index = border.indexOf(pos)

        return index / 4
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

        return neighbouringPositions(pos).map {
            val distance = distanceToCenter(it)
            val neighbour = grid.get(it)

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
     * @param position position the tile would be placed at
     * @return true if the [fromTile] would block a gate when placed at [position]
     */
    private fun isBlockingGates(fromTile: Tile, position: Pair<Int, Int>): Boolean {
        val idx = getBorderingGateIndex(position) ?: return false
        val edgeB = (idx + 1) % 6

        return fromTile.paths[idx] == edgeB
    }

    /**
     * get a list of all positions of the outer hexagon ring in clockwise
     * order starting at the topmost treasure tile (0,-4)
     */
    private fun getOuterPositions(): List<Pair<Int, Int>> {
        var cur = Pair(0, -4)
        val visited: HashSet<Pair<Int, Int>> = HashSet(24)

        return (0..23).map {
            val old = cur
            visited.add(old)

            // .first fails at index 23 because all neighbours are already in visited
            if (it < 23) {
                cur = neighbouringPositions(cur).first {
                    distanceToCenter(it) == 4 && !visited.contains(it)
                }
            }

            old
        }
    }

    /**
     * Get an array of all position bordering [pos].
     * @param pos position to get neighbours of
     * @returns array of 6 positions bordering [pos]
     */
    private fun neighbouringPositions(pos: Pair<Int, Int>): Array<Pair<Int, Int>> =
        arrayOf(
            Pair(0, -1), Pair(1, -1), Pair(1, 0),
            Pair(0, 1), Pair(-1, 1), Pair(-1, 0)
        ).map {
            val qNei = pos.first + it.first
            val rNei = pos.second + it.second

            Pair(qNei, rNei)
        }.toTypedArray()

    /** Check whether a treasure tile lies at a given position. */
    private fun isTreasureTilePosition(pos: Pair<Int, Int>): Boolean {
        val sCoordinate = -pos.first - pos.second
        val distance = distanceToCenter(pos)
        val hasZero = pos.first == 0 || pos.second == 0 || sCoordinate == 0

        return distance == 0 || (distance == 4 && hasZero)
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

    /**
     * Calculate the distance of some position to the center point (0, 0).
     * @param pos position in axial coordinates
     * @return distance to the center point (a positive or zero integer value)
     */
    private fun distanceToCenter(pos: Pair<Int, Int>): Int =
        listOf(pos.first, pos.second, -pos.first - pos.second).map { abs(it) }.max()

    private fun getPlayers(): List<Player> {
        val game = rootService.currentGame ?: throw IllegalStateException("Game not initialized")
        return game.players
    }


}
