package service

import entity.*
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
        game.board.grid.grid.forEach {position, _ ->
            if(!game.board.grid.grid.containsKey(position)) {
                for (rotation in 0 until 6) {
                    possibleMoves.add(position to rotation)
                }
            }
        }

        //select a random move
        val selectedMove = possibleMoves.random()

        return Pair(currentTile, selectedMove.second)

    }

}