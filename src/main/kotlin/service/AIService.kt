package service

import entity.*
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random

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
 * @property ps Reference to the PlayerService, used to execute moves on behalf of the AI player.
 */
class AIService(private val rootService: RootService) : AbstractRefreshingService() {

    private val ps = rootService.playerService

    //It is needed in order AIService and RootService logically bind them.
    //var AIService = AIService(this)


    /**
     * Calculates the next move for the AI player based on the current game state.
     * This involves evaluating all possible moves and then selecting one at random.
     *
     * @return A Pair, where the first element is a Pair of coordinates representing the position on the board
     * for the chosen tile, and the second element is an Int representing the rotation of the tile.
     */
    fun calculateNextMove(): Pair<Pair<Int, Int>, Int> {
        val game = rootService.currentGame ?: throw IllegalStateException("Game not initialized")

        val currentTile = game.drawPile.first()

        //calculate all possible moves
        val possibleMoves = mutableListOf<Pair<Pair<Int,Int>, Int>>()

        for (row in -4..4) {
            for (col in -4..4) {
                if ((row + col).absoluteValue >= 5 ) {
                    continue
                }

                val position = Pair(col, row)

                //if there is no tile in this pos, add all rotations to possible moves
                if (isValidTilePlacement(position, game)) {
                    for(rotation in 0 until 6) {
                        possibleMoves.add(position to rotation)
                    }
                }
            }
        }

        //select a random move
        val selectedMove = possibleMoves.random()

        //position and rotation
        return Pair(selectedMove.first, selectedMove.second)
    }

/*
    /**
     * Generates and executes a random move for the AI player using the given tile.
     * It randomly selects both a valid board position (not already occupied and not at (0, 0))
     * and a rotation for the tile. The move is then executed via the PlayerService.
     *
     * The function continues to generate random positions until it finds one that is not already occupied.
     * Once a valid position and rotation are determined, the move is executed.
     *
     * @param tile The Tile to be placed by the AI player.
     */
    fun randomMove(tile: Tile) {

        val game = rootService.currentGame ?: throw IllegalStateException("Game not initialized")

        var randomX: Int
        var randomY: Int

        do {
            randomX = Random.nextInt(-4, 5).takeUnless { it == 0 } ?: Random.nextInt(-4, 5)
            randomY = Random.nextInt(-4, 5).takeUnless { it == 0 } ?: Random.nextInt(-4, 5)
        } while (game.board.grid.grid[Pair(randomX, randomY)] != null)

        val randomRotation = Random.nextInt(0, 7)
        // We have a valid position and a random rotation, call the playerMove function
        val newMove = Pair(tile, randomRotation)
        val newPosition = Pair(randomX, randomY)
        ps.playerMove(newMove, newPosition)
    }
    */

    // Constants for heuristic values
    val GEM_VALUE = 5  // Example value, adjust based on your game's balance
    val GEM_DIFFERENCE_VALUE = 10
    val GEM_ON_TILE_VALUE = 3
    val MAX_PROXIMITY_SCORE = 10 // Max score for a gem being next to a gate

    /*fun minimax(gameState: GameState, depth: Int, alpha: Int, beta: Int, maximizingPlayer: Boolean): Int {
        if (depth == 0 || isGameOver(gameState)) {
            return staticEvaluationOfPosition(gameState)
        }

        var newAlpha = alpha
        var newBeta = beta

        if (maximizingPlayer) {
            var maxEval = Int.MIN_VALUE
            val children = generateChildrenOfGameState(gameState)

            for (child in children) {
                val eval = minimax(child, depth - 1, newAlpha, newBeta, false)
                maxEval = maxOf(maxEval, eval)
                newAlpha = maxOf(newAlpha, eval)
                if (newBeta <= newAlpha) break
            }

            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            val children = generateChildrenOfGameState(gameState)

            for (child in children) {
                val eval = minimax(child, depth - 1, newAlpha, newBeta, true)
                minEval = minOf(minEval, eval)
                newBeta = minOf(newBeta, eval)
                if (newBeta <= newAlpha) break
            }

            return minEval
        }
    }*/

    private fun isGameOver(gameState: GameState): Boolean {
        // Implement logic to check if the game is over
        return true
    }

    private fun staticEvaluationOfPosition(gameState: GameState): Int {
        var score = 0

        val aiPlayer = gameState.players.firstOrNull { it.playerType == PlayerType.COMPUTER }
        val humanPlayers = gameState.players.filter { it.playerType == PlayerType.PERSON }

        aiPlayer?.let { ai ->
            // Existing scoring logic
            score += ai.collectedGems.size * GEM_VALUE
            ai.currentTile?.let { currentTile ->
                score += evaluateTilePotential(currentTile)
            }
            humanPlayers.forEach { human ->
                score += (ai.collectedGems.size - human.collectedGems.size) * GEM_DIFFERENCE_VALUE
            }

            // Additional consideration: Score based on gem proximity to the AI's gates

            //score += evaluateGemProximityToGates(gameState, ai)
        }

        return score
    }

    private fun evaluateMaterial(player: Player): Int {
        var materialScore = 0

        // Evaluate the player's collected gems
        materialScore += player.collectedGems.size * GEM_VALUE

        // Evaluate the current tile held by the player
        player.currentTile?.let { tile ->
            materialScore += evaluateTilePotential(tile)
        }

        return materialScore
    }

    private fun evaluateTilePotential(tile: Tile): Int {
        var tileScore = 0

        // Assign points based on the type of tile
        tileScore = when (tile.tileType) {
            TileType.CORNERS_ONLY -> 10
            TileType.STRAIGHT_NOCROSS -> 8
            TileType.CURVES_TO_CORNER -> 12
            TileType.STRAIGHTS_ONLY -> 7
            TileType.LONG_CURVES -> 9
            TileType.TREASURE_CORNER -> 20
            TileType.TREASURE_CENTER -> 25
            // Adjust these values based on your game's strategy
        }

        // Consider additional factors, like gems on the tile
        tileScore += tile.gems.filterNotNull().size * GEM_ON_TILE_VALUE

        return tileScore
    }
/*
    private fun evaluateGemProximityToGates(gameState: GameState, aiPlayer: Player): Int {
        var proximityScore = 0
        val aiGates = getAIGates(aiPlayer) // Implement this method based on AI's gates

        // Define the fixed positions of specific gems
        val centerPosition = Pair(0, 0) // The center position with saphires and emeralds
        val cornerPositions = listOf(
            Pair(-4, 4), Pair(-4, 0), Pair(0, -4),
            Pair(4, -4), Pair(4, 0), Pair(0, 4)
        ) // The corner positions with ambers

        // Calculate scores for the gems in the center
        proximityScore += Gem.SAPHIRE.score() * calculateDistanceToClosestGate(centerPosition, aiGates)
        proximityScore += Gem.EMERALD.score() * 5 * calculateDistanceToClosestGate(centerPosition, aiGates) // 5 emeralds

        // Calculate scores for the gems in the corners
        for (corner in cornerPositions) {
            proximityScore += Gem.AMBER.score() * 6 * calculateDistanceToClosestGate(corner, aiGates) // 6 ambers in total
        }

        // Adjust scores if necessary, depending on whether closer should give more points or less
        // ...

        return proximityScore
    }*/

    private fun getAIGates(aiPlayer: Player): List<Pair<Int, Int>> {
        return listOf() // Placeholder return, implement actual logic
    }

/*    private fun generateChildrenOfGameState(gameState: GameState): List<GameState> {
        val children = mutableListOf<GameState>()

        // Assuming the AI is the current player
        gameState.currentPlayer.currentTile?.let { currentTile ->
            // Generate all possible moves for the current tile
            val possibleMoves = findAllPossibleMoves(gameState, currentTile)

            // Iterate through all possible moves
            for ((position, rotation) in possibleMoves) {
                // Deep copy the current game state to create a new child state
                val childState = gameState.deepCopy()

                // Set the tile's new rotation
                val rotatedTile = currentTile.deepCopy().apply { this.rotation = rotation }

                // Place the tile at the position in the child state
                childState.board.grid.grid[position] = rotatedTile

                // Perform any additional game state updates necessary after placing a tile
                // For example, handle the next player's turn, move gems if applicable, etc.
                // updateGameStateAfterMove(childState, position, rotatedTile)

                // Add the new child state to the list
                children.add(childState)
            }
        }
        return children
    }*/


    /**
     * Calculates the most strategic move for the current player based on the current game state and the current tile.
     * This function analyzes all possible moves from the current state, evaluates them using a heuristic scoring system,
     * and selects the move with the highest score. If no strategically advantageous move is found,
     * it defaults to selecting a random move.
     *
     * @return A Pair, where the first element is a Pair of the current Tile and an Int representing the rotation of the tile,
     * and the second element is a Pair of coordinates representing the position on the board for the chosen tile.
     */
    fun properMoveForAI(game: GameState, currentTile: Tile): Pair<Pair<Tile, Int>, Pair<Int, Int>> {
        var bestScore = Int.MIN_VALUE
        var bestMove: Pair<Pair<Int, Int>, Int>? = null


        val aiPlayer = game.players.firstOrNull { it.playerType == PlayerType.COMPUTER }
        //AI player token set to white if there is no COMPUTER/AI in the game
        val aiPlayerToken: PlayerToken = aiPlayer?.playerToken ?: PlayerToken.WHITE

        if (aiPlayer != null) {
            // Generate all possible moves
            val possibleMoves = findAllPossibleMoves(game, currentTile)

            for (move in possibleMoves) {
                // Apply the move on a temporary game state
                val tempGameState = applyMove(game.copy(), move)

                // Calculate the heuristic score considering the AI player's token
                val score = calculateHeuristicScore(tempGameState, aiPlayerToken)
/*
                // Use minimax to calculate the score considering the AI player's token.
                // The depth, alpha, and beta values would need to be chosen based on how
                // deep you want the AI to calculate. These are often game-specific.
                val score = minimax(tempGameState, depth = 4, alpha = Int.MIN_VALUE, beta = Int.MAX_VALUE, maximizingPlayer = true)
*/
                if (score > bestScore) {
                    bestScore = score
                    bestMove = move
                }
            }
        }

        // No best move found for AI, fallback to a random move
        if (bestMove == null) {
            val fallbackMove = calculateNextMove()
            // Add the currentTile to the return value
            return Pair(Pair(currentTile, fallbackMove.second), fallbackMove.first)
        }

        return Pair(Pair(currentTile, bestMove.second), bestMove.first)
    }

    private fun calculateHeuristicScore(gameState: GameState, aiPlayerToken: PlayerToken): Int {

        var score = 0

        //Distance of the gems to the gates
        score += calculateProximityScore(gameState, aiPlayerToken)

        /**
        //Values of the gems
        score += calculateStoneValueScore(gameState)

        //Blocking the enemy
        score += calculateBlockingScore

        //Condition of the board
        score += calculateBoardStateScore(gameState)
        */

        //additional factors

        return score
    }

    private val MAX_SCORE = 1000

    private fun calculateProximityScore(game: GameState, aiPlayerToken: PlayerToken): Int {
        val OWN_GATE_MULTIPLIER = 2
        var score = 0
        val gemsWithPositions = getGemsWithPositions(game)

        for ((gem, position) in gemsWithPositions) {
            val nearestGateDistance = findNearestGateDistance(position, game.board.gates)
            val nearestGate = findNearestGate(game.board, position)

            if (nearestGateDistance != null && (nearestGate?.first == aiPlayerToken || nearestGate?.second == aiPlayerToken)) {
                var gateScore = MAX_SCORE - nearestGateDistance
                gateScore *= OWN_GATE_MULTIPLIER
                score += gateScore
            }
        }

        return score
    }

    private fun findNearestGate(board: Board, gemPosition: Pair<Int, Int>): Pair<PlayerToken, PlayerToken>? {
        // TODO: Implement logic to find the nearest gate based on gemPosition
        return null
    }

    private fun findNearestGateDistance(gemPosition: Pair<Int, Int>, gates: Array<Pair<PlayerToken, PlayerToken>>): Int? {
        // Use the logic to calculate the distance from gemPosition to each gate
        // TODO:  Return the minimum distance or null if no path to a gate is found.
        var minDistance: Int? = null

        for (gateIndex in gates.indices) {
            val gatePosition = getGatePosition(gates[gateIndex])
            val distance =
                calculatePathDistance(gemPosition, gatePosition)

            if (distance != null && (minDistance == null || distance < minDistance)) {
                minDistance = distance
            }
        }

        return minDistance
    }

    private fun getGatePosition(gatePair: Pair<PlayerToken, PlayerToken>): Pair<Int, Int> {
        // TODO: Logic to extract the gate position from the gate pair.
        return Pair(gatePair.first.ordinal, gatePair.second.ordinal)
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

                // Check if the position is valid (e.g., not a fixed treasure tile) and empty
                if (isValidTilePlacement(position, game)) {
                    // Add all possible rotations (0 to 5) for this position
                    for (rotation in 0 until 6) {
                        possibleMoves.add(Pair(position, rotation))
                    }
                }
            }
        }

        return possibleMoves
    }

    private fun isValidTilePlacement(position: Pair<Int, Int>, game: GameState): Boolean {
        // Check if the position is empty and not a treasure tile
        return game.board.grid.grid[position] == null && !isTreasureTile(position)
    }

    private fun isTreasureTile(position: Pair<Int, Int>): Boolean {
        // Define positions of treasure tiles
        val treasureTilePositions = setOf(Pair(0, -4), Pair(0, 4), Pair(-4, 4), Pair(-4, 0), Pair(4, 0), Pair(4, -4), Pair(0, 0))
        return position in treasureTilePositions
    }

    private fun getGemsWithPositions(game: GameState): List<Pair<Gem, Pair<Int, Int>>> {
        // TODO: Implement logic to get gems with positions from the game state
        return emptyList()
    }
    private fun applyMove(gameState: GameState, move: Pair<Pair<Int, Int>, Int>): GameState {
        // TODO: Implement logic to apply a move to the game state
        return gameState
    }

    fun getAiPlayerTokens(players: List<Player>): List<PlayerToken> {
        return players.filter { it.playerType == PlayerType.COMPUTER }
            .map { it.playerToken }
    }

}