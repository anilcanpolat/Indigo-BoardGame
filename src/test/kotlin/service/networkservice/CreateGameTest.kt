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
    fun testOnGameStartCalled() {
        val host = RootService()
        val guest = RootService()

        val gameStartSemaphore = Semaphore(0)

        host.addRefreshable(object: Refreshable {
            override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
                assert(players.any { it.name == "Alice" }) { "player alice missing" }
                assert(players.any { it.name == "Bob" }) { "player bob missing" }

                gameStartSemaphore.release()
            }
        })

        val sessionID = java.util.Random().nextInt().toString()

        host.networkService.createGame(sessionID, NetworkConfig.ALICE, GameMode.TWO_PLAYERS)

        Thread.sleep(NetworkConfig.TEST_TIMEOUT)

        guest.networkService.joinGame(sessionID, NetworkConfig.BOB)

        assert(gameStartSemaphore.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS)) {
            "waiting for call to onGameStart timed out"
        }

        checkNotNull(host.currentGame)
    }
}