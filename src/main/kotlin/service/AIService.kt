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
    fun randomMove(): Pair<Pair<Int, Int>, Int> {
        val game = rootService.currentGame ?: throw IllegalStateException("Game not initialized")
        val currentTile = game.drawPile.first()

        val possibleMoves = mutableListOf<Pair<Pair<Int, Int>, Int>>()

        for (row in -4..4) {
            for (col in -4..4) {
                if ((row + col).absoluteValue >= 5) {
                    continue
                }

                val position = Pair(col, row)

                for (rotation in 0 until 6) {
                    if (CommonMethods.isValidMove(game, currentTile, rotation, position)) {
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
        return Pair(selectedMove.first, selectedMove.second)
    }
}