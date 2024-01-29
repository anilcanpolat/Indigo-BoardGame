package service

import kotlin.test.*
import org.junit.jupiter.api.Nested

import entity.*
import java.util.Random

/**
 * test cases making sure that AI games are handled correctly both
 * in local and in remote games
 */
class AiIntegrationTest {
    private var rootService = RootService()

    /** replace the old instance of [rootService] with a fresh one */
    @BeforeTest
    fun resetRootService() {
        rootService = RootService()
    }

    /** test cases with two players where one is using the ai */
    @Nested
    inner class TwoPlayerTests {
        /** create a new game with two players */
        @BeforeTest
        fun startGame() {
            val players = listOf(
                PlayerConfig("Alice", -1, PlayerType.PERSON),
                PlayerConfig("Bob", -1, PlayerType.COMPUTER, useRandomAI = true)
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

        /** make sure that some moves can be played with the ai */
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

            repeat(6) {
                val move = CommonMethods.calculateRandomAIMove(gameState())
                rootService.playerService.playerMove(move.first, move.second)
            }

            assertEquals(moves, 12, "ai did not play the correct amount of moves")
        }

        /** test a game with two ai players connected over the network */
        @Test
        fun aiGameTest() {
            val host = RootService()
            val session = Random().nextInt().toString()

            host.networkService.createGame(session,
                PlayerConfig("Alice", -1, PlayerType.PERSON, useRandomAI = true),
                GameMode.TWO_PLAYERS)
            Thread.sleep(1000)

            val guest = RootService()
            guest.networkService.joinGame(session, PlayerConfig("Bob", -1, PlayerType.COMPUTER, useRandomAI = true))

            Thread.sleep(1000)

            var moves = 0

            host.playerService.addRefreshable(object : Refreshable {
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

            host.playerService.playerMove(Pair(currentTile(), 0), Pair(-1, 1))

            Thread.sleep(2000)
            assertEquals(moves, 2)
        }
    }

    private fun gameState(): GameState =
        checkNotNull(rootService.currentGame) { "game not initialized" }

    private fun currentPlayer(): Player =
        gameState().currentPlayer

    private fun currentTile(): Tile =
        checkNotNull(currentPlayer().currentTile) { "no tile held" }

}