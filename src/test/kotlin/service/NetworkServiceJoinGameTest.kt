package service

import java.util.concurrent.Semaphore
import entity.GameMode
import entity.Player
import entity.PlayerToken
import kotlin.test.*

/** test cases for [NetworkService.joinGame] */
class NetworkServiceJoinGameTest {
    /** make sure that [Refreshable.onGameStart] is called in a guest when the host starts the game */
    @Test
    fun joinGameTest() {
        val host = RootService()
        val guest = RootService()

        val sessionID = java.util.Random().nextInt().toString()
        var onGameStartCalled = false
        val gameStartSemaphore = Semaphore(0)

        guest.networkService.addRefreshable(object: Refreshable {
            override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
                check(players.any { it.name == "Alice" })
                check(players.any { it.name == "Bob" })

                onGameStartCalled = true
                gameStartSemaphore.release()
            }
        })

        host.networkService.createGame(sessionID, "Alice", GameMode.TWO_PLAYERS)
        Thread.sleep(1000)

        guest.networkService.joinGame(sessionID, "Bob")
        gameStartSemaphore.acquire()

        check(onGameStartCalled)
    }
}