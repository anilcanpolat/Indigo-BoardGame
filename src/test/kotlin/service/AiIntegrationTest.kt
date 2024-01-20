package service

import kotlin.test.*
import org.junit.jupiter.api.Nested

import entity.*

class AiIntegrationTest {
    private var rootService = RootService()

    @BeforeTest
    fun resetRootService() {
        rootService = RootService()
    }

    @Nested
    inner class TwoPlayerTests() {
        @BeforeTest
        fun startGame() {
            val players = listOf(
                PlayerConfig("Alice", -1, PlayerType.PERSON),
                PlayerConfig("Bob", -1, PlayerType.COMPUTER, useRandomAI = false)
            )

            rootService.startGame(players, GameMode.TWO_PLAYERS)
        }

        /** make sure that onWaitForInput is called when the player is in turn */
        @Test
        fun waitForInputCalled() {
            var callbackReceived = false

            rootService.playerService.addRefreshable(object : Refreshable {
                override fun onWaitForInput() {
                    callbackReceived = true
                }
            })

            rootService.playerService.playerMove(Pair(currentTile(), 0), Pair(1, -1))
            assertTrue(callbackReceived, "missing onWaitForInput refresh")
        }

        @Test
        fun randomGameTest() {
            var moves = 0

            rootService.playerService.addRefreshable(object : Refreshable {
                override fun onPlayerMove(
                    player: Player,
                    nextPlayer: Player,
                    tile: Tile,
                    position: Pair<Int, Int>,
                    rotation: Int
                ) {
                    moves += 1
                }
            })

            for (i in 0..5) {
                val move = CommonMethods.calculateRandomAIMove(gameState())
                rootService.playerService.playerMove(move.first, move.second)
            }

            assertEquals(moves, 12, "ai did not play the correct amount of moves")
        }
    }

    private fun gameState(): GameState =
        checkNotNull(rootService.currentGame) { "game not initialized" }

    private fun currentPlayer(): Player =
        gameState().currentPlayer

    private fun currentTile(): Tile =
        checkNotNull(currentPlayer().currentTile) { "no tile held" }

}