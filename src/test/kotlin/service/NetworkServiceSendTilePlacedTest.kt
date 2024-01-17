package service

import entity.GameMode
import entity.Player
import entity.PlayerToken
import kotlin.test.*
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/** test cases for [NetworkService.sendTilePlaced] */
class NetworkServiceSendTilePlacedTest {
    private var host = RootService()
    private var guest = RootService()
    private var sessionID = ""

    /**
     * create a new two player game with the players "Alice" (Host) and "Bob" (Guest)
     */
    @BeforeTest
    fun createTwoPlayerGame() {
        host = RootService()
        guest = RootService()
        sessionID = java.util.Random().nextInt().toString()
        val lock = Semaphore(0)

        host.networkService.createGame(sessionID, "Alice", GameMode.TWO_PLAYERS)

        Thread.sleep(1000)

        guest.networkService.addRefreshable(object : Refreshable {
            override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
                lock.release()
            }
        })

        guest.networkService.joinGame(sessionID, "Bob")

        check(lock.tryAcquire(1, TimeUnit.MINUTES)) {
            "waiting for call to onGameStart timed out"
        }
    }

    /** make sure that valid tile placement messages can be received by other players */
    @Test
    fun sendMessageTest() {
        if (hostInTurn()) {
            host.networkService.sendTilePlaced(0, Pair(1, 1))
        } else {
            guest.networkService.sendTilePlaced(0, Pair(1, 1))
        }
    }

    /** check whether the host (Alice) or the guest (Bob) is in turn */
    private fun hostInTurn(): Boolean {
        val hostState = checkNotNull(host.currentGame)
        return hostState.currentPlayer.name == "Alice"
    }
}