package service.networkservice

import entity.GameMode
import entity.Player
import entity.PlayerToken

import service.Refreshable
import service.RootService

import kotlin.test.*

import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/** test cases for [service.NetworkService.createGame] */
class CreateGameTest {
    /**
     * Create two clients, one as a host and one as a guest. Have one join the other.
     * Make sure that this will start the game.
     */
    @Test
    fun createGameTest() {
        val host = RootService()
        val guest = RootService()

        var gameStartCalled = false
        val gameStartSemaphore = Semaphore(0)

        host.addRefreshable(object: Refreshable {
            override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
                assert(players.any { it.name == "Alice" }) { "player alice missing" }
                assert(players.any { it.name == "Bob" }) { "player bob missing" }

                gameStartCalled = true
                gameStartSemaphore.release()
            }
        })

        val sessionID = java.util.Random().nextInt().toString()

        host.networkService.createGame(sessionID, "Alice", GameMode.TWO_PLAYERS)

        Thread.sleep(1000)

        guest.networkService.joinGame(sessionID, "Bob")

        assert(gameStartSemaphore.tryAcquire(1, TimeUnit.MINUTES)) {
            "waiting for call to onGameStart timed out"
        }

        assertTrue(gameStartCalled)
        checkNotNull(host.currentGame)
    }
}