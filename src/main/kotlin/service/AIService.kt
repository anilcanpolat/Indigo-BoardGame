package service

import entity.*
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
     * @return A Pair of Tile and Int, representing the chosen tile and its rotation.
     */
    fun calculateNextMove(): Pair<Tile, Int> {
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

        return Pair(currentTile, selectedMove.second)

    }

    /**
    * Calculates the most strategic move for the current player based on the current game state and the current tile.
    * This function analyzes all possible moves from the current state, evaluates them using a heuristic scoring system,
    * and selects the move with the highest score. If no strategically advantageous move is found,
    * it defaults to selecting a random move.
    */
    fun properMove(game: GameState, currentTile: Tile): Pair<Pair<Int, Int>, Int> {

        var bestScore = Int.MIN_VALUE
        var bestMove: Pair<Pair<Int, Int>, Int>? = null

        //Generate all possible moves
        val possibleMoves = findAllPossibleMoves(game,currentTile)

        for(move in possibleMoves) {
            //apply on temp gamestate
            val tempGameState = applyMove(game.copy(),move)

            val score = calculateHeuristicScore(tempGameState)

            if(score > bestScore) {
                bestScore = score
                bestMove = move
            }
        }
        return bestMove ?:calculateNextMove(game,currentTile)
    }

    private fun calculateHeuristicScore(gameState: GameState): Int {
        var score = 0

        /**
        //Entfernung der Gems zu Toren
        score += calculateProximityScore(gameState)

        //Werte der Steine
        score += calculateStoneValueScore(gameState)

        //Gegner blockieren
        score += calculateBlockingScore

        //Zustand des Spielbretts
        score += calculateBoardStateScore(gameState)
        */

        //additional factors

        return score
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

    private fun applyMove(gameState: GameState, move: Pair<Pair<Int, Int>, Int>): GameState {
        // TODO: Implement logic to apply a move to the game state
        return gameState
    }

    private fun calculateNextMove(game: GameState, currentTile: Tile): Pair<Pair<Int, Int>, Int> {
        // TODO: Implement calculateNextMove
        return Pair(Pair(0, 0), 0)
    }
}