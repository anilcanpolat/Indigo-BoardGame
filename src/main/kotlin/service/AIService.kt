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