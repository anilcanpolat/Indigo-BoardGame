package service

import entity.GameMode
import entity.Player
import entity.PlayerToken
import kotlinx.coroutines.runBlocking
import kotlin.test.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/** test cases for [NetworkService.createGame] */
class NetworkServiceCreateGameTest {
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
                check(players.any { it.name == "Alice" })
                check(players.any { it.name == "Bob" })

                gameStartCalled = true
                gameStartSemaphore.release()
            }
        })

        val sessionID = java.util.Random().nextInt().toString()

        runBlocking {
            host.networkService.createGame(sessionID, "Alice", GameMode.TWO_PLAYERS)
        }

        guest.networkService.joinGame(sessionID, "Bob")
        gameStartSemaphore.tryAcquire(1, TimeUnit.SECONDS)

        check(gameStartCalled)
        checkNotNull(host.currentGame)
    }
}