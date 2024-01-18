package service

import entity.*
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlinx.coroutines.*

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

    // Constants for heuristic values, these are all example values and gonna change them later
    val GEM_VALUE = 5
    val GEM_DIFFERENCE_VALUE = 10
    val GEM_PROXIMITY_WEIGHT = 2

    /**
     * Calculates the best move for the AI player using the minimax algorithm within a given time frame.
     * If the calculation takes longer than the specified timeout, a random move is chosen.
     * @return A Pair of coordinates and rotation for the chosen move.
     */
    suspend fun calculateBestMoveWithTimeout(): Pair<Pair<Int, Int>, Int> {
        val game = rootService.currentGame ?: throw IllegalStateException("Game not initialized")
        val currentTile = game.drawPile.first()
        val possibleMoves = findAllPossibleMoves(game, currentTile)

        return withTimeoutOrNull(10_000) { // 10 seconds timeout
            calculateBestMove()
        } ?: possibleMoves.random() // Fallback to a random move
    }

    /**
     * Calculates the best move for the AI player using the minimax algorithm.
     * @return A Pair, where the first element is a Pair of coordinates representing the position on the board
     * for the chosen tile, and the second element is an Int representing the rotation of the tile.
     */
    fun calculateBestMove(): Pair<Pair<Int, Int>, Int> {
        val game = rootService.currentGame ?: throw IllegalStateException("Game not initialized")
        val currentTile = game.drawPile.first()

        // Generate all possible moves for the current state
        val possibleMoves = findAllPossibleMoves(game, currentTile)

        var bestMove: Pair<Pair<Int, Int>, Int>? = null
        var bestScore = Int.MIN_VALUE

        // Iterate through all possible moves and use minimax to evaluate them
        for (move in possibleMoves) {
            // Create a new game state for this move
            val newState = game.deepCopy().apply {
                currentPlayer.currentTile?.let { tile ->
                    tile.rotation = move.second
                    board.grid.grid[move.first] = tile
                }
            }

            // Evaluate the move using the minimax algorithm
            val moveScore = minimax(newState,
                depth = 3,   //Depth can be changed later
                alpha = Int.MIN_VALUE,
                beta = Int.MAX_VALUE,
                maximizingPlayer = true)

            // Update best move if this move has a better score
            if (moveScore > bestScore) {
                bestScore = moveScore
                bestMove = move
            }
        }

        // Return the best move found or a random move if no best move was identified
        return bestMove ?: possibleMoves.random()
    }

    /**
     * The minimax function for evaluating the best move in the game.
     * @param gameState The current state of the game.
     * @param depth The depth of the search.
     * @param alpha Alpha value for alpha-beta pruning.
     * @param beta Beta value for alpha-beta pruning.
     * @param maximizingPlayer A flag to determine if the current move is by a maximizing player.
     * @return The evaluation score of the game state.
     */
    fun minimax(gameState: GameState, depth: Int, alpha: Int, beta: Int, maximizingPlayer: Boolean): Int {
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
                if (newBeta <= newAlpha) break  // Alpha-beta pruning
            }

            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            val children = generateChildrenOfGameState(gameState)

            for (child in children) {
                val eval = minimax(child, depth - 1, newAlpha, newBeta, true)
                minEval = minOf(minEval, eval)
                newBeta = minOf(newBeta, eval)
                if (newBeta <= newAlpha) break // Alpha-beta pruning
            }
            return minEval
        }
    }

    /**
     * Checks if the game is over.
     * @param gameState The current state of the game.
     * @return True if the game is over, otherwise False.
     */
    private fun isGameOver(gameState: GameState): Boolean {
        return gameState.drawPile.isEmpty()
    }

    /**
     * Generates all possible future game states from the current state.
     * @param gameState The current state of the game.
     * @return A list of possible future game states.
     */
    private fun generateChildrenOfGameState(gameState: GameState): List<GameState> {
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

                // Add the new child state to the list
                children.add(childState)
            }
        }
        return children
    }

    /**
     * Performs a static evaluation of the given game state using weighted heuristics.
     * @param gameState The game state to evaluate.
     * @return The weighted score of the game state.
     */
    private fun staticEvaluationOfPosition(gameState: GameState): Int {
        var score = 0

        val aiPlayer = gameState.players.firstOrNull { it.playerType == PlayerType.COMPUTER }
        val humanPlayers = gameState.players.filter { it.playerType == PlayerType.PERSON }

        aiPlayer?.let { ai ->
            // Base score from the number of collected gems
            val gemScore = ai.collectedGems.size * GEM_VALUE

            // Score based on the difference in the number of gems between AI and human players
            val gemDifferenceScore = humanPlayers.sumOf { human ->
                (ai.collectedGems.size - human.collectedGems.size) * GEM_DIFFERENCE_VALUE
            }

            // Score based on gem proximity to AI's gates, weighted by importance
            val gemProximityScore = evaluateGemProximityToGates(gameState, ai) * GEM_PROXIMITY_WEIGHT

            // Combine all heuristic scores into the total score, adjusting weights as necessary
            score += gemScore + gemDifferenceScore + gemProximityScore
        }

        return score
    }

    /**
     * Evaluates the proximity of specific gems to the gates controlled by the AI player.
     * This function is used in the heuristic evaluation of the game state.
     * @param gameState The current game state.
     * @param aiPlayer The AI player whose gate proximity is being evaluated.
     * @return The total proximity score based on the AI's gates and gem positions.
     */
    private fun evaluateGemProximityToGates(gameState: GameState, aiPlayer: Player): Int {
        var proximityScore = 0
        val aiGates = getAIGates(aiPlayer, gameState.board) // Implement this method based on AI's gates

        // Define the fixed positions of specific gems
        val centerPosition = Pair(0, 0) // The center position with saphires and emeralds
        val cornerPositions = listOf(
            Pair(-4, 4), Pair(-4, 0), Pair(0, -4),
            Pair(4, -4), Pair(4, 0), Pair(0, 4)
        ) // The corner positions with ambers

        // Calculate scores for the gems in the center
        proximityScore += Gem.SAPHIRE.score() * calculateDistanceToClosestGate(centerPosition, aiGates)
        // 5 emeralds
        proximityScore += Gem.EMERALD.score() * 5 * calculateDistanceToClosestGate(centerPosition, aiGates)

        // Calculate scores for the gems in the corners
        for (corner in cornerPositions) {
            // 6 ambers in total
            //proximityScore += Gem.AMBER.score() * 6 * calculateDistanceToClosestGate(corner, aiGates)
        }

        return proximityScore
    }

    // Gate coordinates assigned to the gates
    val gateNumberToCoordinatesMap = mapOf(
        1 to listOf(Pair(1, -5), Pair(2, -5), Pair(3, -5), Pair(4, -5)), // Coordinates for gate 1
        2 to listOf(Pair(5, -4), Pair(5, -3), Pair(5, -2), Pair(5, -1)), // Coordinates for gate 2
        3 to listOf(Pair(4, 1), Pair(3, 2), Pair(2, 3), Pair(1, 4)), // Coordinates for gate 3
        4 to listOf(Pair(-4, -5), Pair(-3, 5), Pair(-2, 5), Pair(-1, 5)), // Coordinates for gate 4
        5 to listOf(Pair(-5, 1), Pair(-5, 2), Pair(-5, 3), Pair(-5, 4)), // Coordinates for gate 5
        6 to listOf(Pair(-1, -4), Pair(-2, -3), Pair(-3, -2), Pair(-4, -1)), // Coordinates for gate 6
    )

    /**
     * Retrieves a list of gate coordinates controlled by the AI player.
     * @param aiPlayer The AI player whose gates are to be identified.
     * @param board The game board with gate information.
     * @return A list of gate coordinates controlled by the AI player.
     */
    private fun getAIGates(aiPlayer: Player, board: Board): List<Pair<Int, Int>> {
        val aiGateNumbers = board.gates
            .mapIndexedNotNull { index, pair ->
                if (pair.first == aiPlayer.playerToken || pair.second == aiPlayer.playerToken) index + 1 else null
            }

        // Flatten the list of lists into a single list of coordinates.
        return aiGateNumbers.flatMap { gateNumber ->
            gateNumberToCoordinatesMap[gateNumber] ?: emptyList()
        }
    }

    /**
     * Calculates the minimum distance from a position to the closest AI-controlled gate.
     * @param position The position (coordinates) from which to calculate the distance.
     * @param aiGates A list of AI-controlled gates' coordinates.
     * @return The minimum distance to the closest AI-controlled gate.
     */
    private fun calculateDistanceToClosestGate(position: Pair<Int, Int>, aiGates: List<Pair<Int, Int>>): Int {
        val positionCube = hexToCube(position)  // Convert hex to cube coordinates for distance calculation

        // Find the minimum distance to any of the AI-controlled gates
        return aiGates
            .map { hexToCube(it) }
            .minOfOrNull { gateCube -> cubeDistance(positionCube, gateCube) } ?: Int.MAX_VALUE
    }

    /**
     * Converts hexagonal grid coordinates to cube coordinates.
     * @param hex The hexagonal grid coordinates.
     * @return The equivalent cube coordinates.
     */
    private fun hexToCube(hex: Pair<Int, Int>): Triple<Int, Int, Int> {
        val (x, y) = hex
        val z = -x - y  // Cube coordinate z is derived to ensure x + y + z = 0
        return Triple(x, y, z)
    }

    /**
     * Calculates the distance between two points in cube coordinates.
     * @param a The first set of cube coordinates.
     * @param b The second set of cube coordinates.
     * @return The distance between the two points in cube coordinates.
     */
    private fun cubeDistance(a: Triple<Int, Int, Int>, b: Triple<Int, Int, Int>): Int {
        // The maximum of the absolute differences of the coordinates gives the distance
        return maxOf(abs(a.first - b.first), abs(a.second - b.second), abs(a.third - b.third))
    }

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
                val score = minimax(tempGameState,
                depth = 4,  //Depth can be changed later
                alpha = Int.MIN_VALUE,
                beta = Int.MAX_VALUE,
                maximizingPlayer = true)
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