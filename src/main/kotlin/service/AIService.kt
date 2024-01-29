package service

import entity.*
import kotlin.math.abs
import kotlin.math.absoluteValue
import service.CommonMethods.neighbouringPositions

/**
 * AIService is responsible for managing the artificial intelligence aspects of the game.
 * It operates within the context of a given game state, provided by the RootService.
 *
 * This service computes and executes AI moves, making decisions based on the current
 * state of the game. It also contains utility functions to support AI decision-making,
 * such as generating random moves and selecting moves from a set of possibilities.
 *
 * @property rootService Reference to the RootService,
 * which provides access to the current game state and other services.
 * ps was Reference to the PlayerService, used to execute moves on behalf of the AI player.
 */
class AIService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * Calculates the next move for the AI player based on the current game state.
     * This involves evaluating all possible moves and then selecting one at random.
     *
     * @return A Pair, where the first element is a Pair of coordinates representing the position on the board
     * for the chosen tile, and the second element is an Int representing the rotation of the tile.
     */
    fun randomMove(): Pair<Pair<Tile, Int>, Pair<Int, Int>> {

        val game = rootService.currentGame ?: throw IllegalStateException("Game not initialized")

        val possibleMoves = mutableListOf<Pair<Pair<Int, Int>, Int>>()

        for (row in -4..4) {
            for (col in -4..4) {
                if ((row + col).absoluteValue >= 5) {
                    continue
                }

                val position = Pair(col, row)

                for (rotation in 0 until 6) {
                    if (CommonMethods.isValidMove(game, game.currentPlayer.currentTile!!, rotation, position)) {
                        possibleMoves.add(position to rotation)
                    }
                }
            }
        }

        if (possibleMoves.isEmpty()) {
            throw IllegalStateException("No valid move found")
        }

        val selectedMove = possibleMoves.random()

        //Position und Rotation
        return Pair(Pair(game.currentPlayer.currentTile!!, selectedMove.second), selectedMove.first)
    }
    /**
     * -----------------------------------------------------------------------------------------------------------------
     */


    /**
     * Calculates the most strategic move for the current player based on the current game state and the current tile.
     * This function analyzes all possible moves from the current state,
     * evaluates them using a heuristic scoring system,
     * and selects the move with the highest score. If no strategically advantageous move is found,
     * it defaults to selecting a random move.
     *
     * @return A Pair,
     * where the first element is a Pair of the current Tile and an Int representing the rotation of the tile,
     * and the second element is a Pair of coordinates representing the position on the board for the chosen tile.
     */
    fun properMoveForAI(): Pair<Pair<Tile, Int>, Pair<Int, Int>>  {
        val game = rootService.currentGame!!
        val currentTile = game.currentPlayer.currentTile!!
        var bestScore = Int.MIN_VALUE
        var bestMove: Pair<Pair<Int, Int>, Int>? = null

        val aiPlayer = game.currentPlayer
        val aiPlayerToken = aiPlayer.playerToken

        val startTime = System.currentTimeMillis()
        val timeLimit = 9_000 // 10 seconds in milliseconds

        val possibleMoves = findAllPossibleMoves(game, currentTile)

        for (move in possibleMoves) {

            if (System.currentTimeMillis() - startTime > timeLimit) {
                println("Time limit exceeded, choosing best move so far.")
                return Pair(Pair(currentTile, bestMove!!.second), bestMove.first)
            }
            // Apply the move on a temporary game state
            val tempGameState = applyMove(game.deepCopy(), move)
            // Calculate the heuristic score considering the AI player's token
            val score = calculateHeuristicScore(tempGameState, aiPlayerToken)

            if (score >= bestScore) {
                bestScore = score
                bestMove = move
            }
        }

        /*print("BestScore: ")
        println(bestScore)*/
        if (bestMove == null) return randomMove()

        return Pair(Pair(currentTile, bestMove.second), bestMove.first)
    }

    private fun calculateHeuristicScore(gameState: GameState, aiPlayerToken: PlayerToken): Int {

        var score = 0

        //Distance of the gems to the gates
        score += calculateProximityScore(gameState, aiPlayerToken)

        //under this line it will give either 1 or 0
        // Securing a Point
        score += calculateGemSecure(gameState) * 100  // High priority

        // Blocking the enemy
        score += calculateBlockingScore(gameState) * 50  // Medium priority

        //Additional methods can be added

        return score
    }

    private val maxScore = 10

    private fun calculateProximityScore(game: GameState, aiPlayerToken: PlayerToken): Int {
        var score = 0
        val gemsWithPositions = getGemsWithPositions(game)

        for ((gem, position) in gemsWithPositions) {
            // calculate the closest gate and the distance
            val (nearestGate, nearestGateDistance) = findNearestGateAndDistance(game.board, position)

            // give points if it is self gate
            if (nearestGateDistance != null &&
                (nearestGate?.first == aiPlayerToken || nearestGate?.second == aiPlayerToken)) {
                var gateScore = maxScore - nearestGateDistance
                gateScore += gem.score() * 2 // add gem's value
                score += gateScore
            }
        }

        return score
    }

    private fun calculateGemSecure(gameState: GameState): Int {
        // Check the gems size on the previous state(before the move)
        val previousState = gameState.previousState
        if (previousState != null) {
            val previousGemCount = previousState.currentPlayer.collectedGems.size
            val currentGemCount = gameState.currentPlayer.collectedGems.size

            // If the current gem count is greater than the previous, the move secured a gem
            if (currentGemCount > previousGemCount) {
                return 1
            }
        }
        return 0
    }

    //these are 3rd circle of the hexagon which is when we need to block the opponent
    private val keyPositions = setOf(
        Pair(0, -3), Pair(1, -3), Pair(2, -3), Pair(3, -3), Pair(3, -2), Pair(3, -1), Pair(3, 0), Pair(2, 1),
        Pair(1, 2), Pair(0, 3), Pair(-1, 3), Pair(-2, 3), Pair(-3, 3), Pair(-3, +2), Pair(-3, +1), Pair(-3, 0),
        Pair(-2, -1), Pair(-1, -2),
    )

    private fun calculateBlockingScore(gameState: GameState): Int {
        var blockingScore = 0

        // Iterate through the key positions
        for (keyPoint in keyPositions) {
            val previousState = gameState.previousState

            // Check if there was a previous state to compare against
            if (previousState != null) {
                // Get the gem at the key position in the previous state
                val gemAtKeyPoint = getGemAtPosition(previousState, keyPoint)

                if (gemAtKeyPoint != null) {
                    // Get the gem's new position in the current state
                    val newPosition = getGemPosition(gameState, gemAtKeyPoint)

                    if (newPosition != null) {
                        // Check if the gem's distance to the center is now between 1 and 3
                        val distanceToCenter = CommonMethods.distanceToCenter(newPosition)

                        if (distanceToCenter in 1..3) {
                            blockingScore = 1
                        }
                    }
                }
            }
        }

        return blockingScore
    }

    private fun getGemAtPosition(gameState: GameState, position: Pair<Int, Int>): Gem? {
        val tile = gameState.board.grid.grid[position]
        return tile?.gems?.firstOrNull { it != null }
    }

    private fun findNearestGateAndDistance(board: Board,
                                           gemPosition: Pair<Int, Int>): Pair<Pair<PlayerToken, PlayerToken>?, Int?> {
        var nearestGate: Pair<PlayerToken, PlayerToken>? = null
        var minDistance: Int? = null

        board.gates.forEachIndexed { index, gatePair ->
            val gatePositionsList = getGatePositions(index)

            gatePositionsList.forEach { gatePosition ->
                val distance = calculatePathDistance(gemPosition, gatePosition)
                if (minDistance == null || distance < (minDistance ?: Int.MAX_VALUE)) {
                    minDistance = distance
                    nearestGate = gatePair
                }
            }
        }

        return Pair(nearestGate, minDistance)
    }

    // positions of the tiles which are near the gates
    private val gatePositions = mapOf(
        0 to listOf(Pair(1, -4), Pair(2, -4), Pair(3, -4)),
        1 to listOf(Pair(4, -3), Pair(4, -2), Pair(4, -1)),
        2 to listOf(Pair(3, 1), Pair(2, 2), Pair(1, 3)),
        3 to listOf(Pair(-1, 4), Pair(-2, 4), Pair(-3, 4)),
        4 to listOf(Pair(-4, 3), Pair(-4, 2), Pair(-4, 1)),
        5 to listOf(Pair(-3, -1), Pair(-2, -2), Pair(-1, -3))
    )

    private fun getGatePositions(gateNumber: Int): List<Pair<Int, Int>> {
        return gatePositions[gateNumber] ?: throw IllegalArgumentException("Invalid gate number: $gateNumber")
    }

    private fun calculatePathDistance(startPos: Pair<Int, Int>, endPos: Pair<Int, Int>): Int {
        // Convert axial coordinates to relative distance
        val dx = endPos.first - startPos.first
        val dy = endPos.second - startPos.second
        val dz = -dx - dy
        return maxOf(abs(dx), abs(dy), abs(dz))
    }

    private fun findAllPossibleMoves(game: GameState, currentTile: Tile): List<Pair<Pair<Int, Int>, Int>> {
        val possibleMoves = mutableListOf<Pair<Pair<Int, Int>, Int>>()

        // Iterate through each position on the board
        for (row in -4..4) {
            for (col in -4..4) {
                val position = Pair(col, row)

                // Add all possible rotations (0 to 5) for this position if the move is valid
                for (rotation in 0 until 6) {
                    if (CommonMethods.isValidMove(game, currentTile, rotation, position)) {
                        possibleMoves.add(Pair(position, rotation))
                    }
                }
            }
        }

        return possibleMoves
    }

    private fun getGemsWithPositions(game: GameState): List<Pair<Gem, Pair<Int, Int>>> {
        val gemsWithPositions = mutableListOf<Pair<Gem, Pair<Int, Int>>>()

        // iterate board
        for ((position, tile) in game.board.grid.grid.entries) {
            // check for gems
            tile.gems.forEachIndexed { _, gem ->
                if (gem != null) {
                    gemsWithPositions.add(Pair(gem, position))
                }
            }
        }

        return gemsWithPositions
    }

    private fun getGemPosition(gameState: GameState, gem: Gem): Pair<Int, Int>? {

        for ((position, tile) in gameState.board.grid.grid.entries) {
            if (tile.gems.contains(gem)) {
                return position
            }
        }
        return null
    }

    /**
    //Simulation for the AI for applyMove
     */
    private fun applyMove(gameState: GameState, move: Pair<Pair<Int, Int>, Int>): GameState {
        val modifiedGameState = gameState.deepCopy()
        val (position, rotation) = move
        val tile = gameState.currentPlayer.currentTile ?: throw IllegalStateException("Current player has no tile.")

        // Rotate and place the tile
        tile.rotate(rotation)
        modifiedGameState.board.grid.grid[position] = tile

        // Process gem movements
        processGemMovementsAfterTilePlacement(modifiedGameState, tile, position)

        // Update the next player
        val nextPlayerIndex = (gameState.players.indexOf(gameState.currentPlayer) + 1) % gameState.players.size
        modifiedGameState.currentPlayer = gameState.players[nextPlayerIndex]

        // Handle the draw pile
        if (modifiedGameState.drawPile.isNotEmpty()) {
            modifiedGameState.currentPlayer.currentTile = modifiedGameState.drawPile.removeAt(0)
        } else {
            modifiedGameState.currentPlayer.currentTile = null
        }

        return modifiedGameState
    }

    private fun processGemMovementsAfterTilePlacement(gameState: GameState,
                                                      placedTile: Tile, position: Pair<Int, Int>) {
        val neighbours = getNeighboursOf(gameState, position)

        for (i in 0..5) {
            val currentNeighbour = neighbours[i] ?: continue
            val neighbourEdge = (i + 3) % 6

            // Handle gem movement logic similar to moveGemToEnd
            val gem = currentNeighbour.gems[i]
            if (gem != null) {
                processGemMovement(gameState, currentNeighbour, placedTile, neighbourEdge, gem)
            }
        }
    }

    private fun processGemMovement(gameState: GameState, fromTile: Tile, toTile: Tile, oldEdge: Int, gem: Gem) {
        // Find the new edge for the gem to move to
        val newEdge = toTile.paths[oldEdge]

        if (newEdge == null || toTile.gems[newEdge] != null) {
            // Handle gem collision or end of path (e.g., scoring, removing gem)
            handleGemCollisionOrEndOfPath(gameState, gem, fromTile, toTile, oldEdge, newEdge)
        } else {
            // Move the gem one step to the new tile and new edge
            toTile.gems[newEdge] = gem
            fromTile.gems[oldEdge] = null

            // Determine the position of the toTile
            val toTilePosition = getTilePosition(toTile) ?: return // If position is null, exit the function

            // Determine the position of the next tile
            val nextTilePosition = neighbouringPositions(toTilePosition)[newEdge]

            // Get the next tile from the game state
            val nextTile = gameState.board.grid.grid[nextTilePosition]

            // If nextTile is valid, continue the recursion
            if (nextTile != null) {
                processGemMovement(gameState, toTile, nextTile, newEdge, gem)
            }
        }
    }

    private fun handleGemCollisionOrEndOfPath(gameState: GameState, gem: Gem,
                                              fromTile: Tile, toTile: Tile, fromEdge: Int, toEdge: Int?) {
        // Check if the gem has reached the end of a path or collided with another gem
        if (toEdge == null || toTile.gems[toEdge] != null) {
            // Handle the end of path or collision
            // If there is a collision, remove both gems from their respective tiles
            fromTile.gems[fromEdge] = null
            if (toEdge != null) {
                toTile.gems[toEdge] = null
            }

            // If the gem reached a gate, allocate points to the player(s)
            if (toEdge != null) {
                val borderingGates = getBorderingGates(toTile, toEdge)
                if (borderingGates != null) {
                    allocatePointsToPlayers(gameState, gem, borderingGates)
                }
            }
        }
    }

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

    private fun allocatePointsToPlayers(gameState: GameState, gem: Gem,
                                        borderingGates: Pair<PlayerToken, PlayerToken>) {
        gameState.players.forEach { player ->
            if (player.playerToken == borderingGates.first || player.playerToken == borderingGates.second) {
                // Add the gem to the player's collected gems
                player.collectedGems.add(gem)
            }
        }
    }

    private fun getNeighboursOf(gameState: GameState, position: Pair<Int, Int>): Array<Tile?> {
        val neighbouringPositions = neighbouringPositions(position)
        return neighbouringPositions.map { pos ->
            gameState.board.grid.grid[pos]
        }.toTypedArray()
    }

    private fun getTilePosition(tile: Tile): Pair<Int, Int>? {
        val state = checkNotNull(rootService.currentGame)
        val grid = state.board.grid.grid

        return grid.keys.find {
            grid[it] === tile
        }
    }
}