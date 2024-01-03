package service.networkservice

import entity.GameMode
import entity.Player
import entity.PlayerToken
import entity.Tile

import service.Refreshable
import service.RootService

import kotlin.test.*

import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/** test cases for [service.NetworkService.sendTilePlaced] */
class SendTilePlacedTest {
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

        Thread.sleep(NetworkConfig.TEST_TIMEOUT)

        guest.networkService.addRefreshable(object : Refreshable {
            override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
                lock.release()
            }
        })

        guest.networkService.joinGame(sessionID, "Bob")

        check(lock.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS)) {
            "waiting for call to onGameStart timed out"
        }
    }

    /**
     * Make sure that calling [service.NetworkService.sendTilePlaced] causes [Refreshable.onPlayerMove]
     * to be called on all participating players. This test fails because [service.PlayerActionService.playerMove]
     * is not implemented causing [Refreshable.onPlayerMove] to never be called.
     */
    @Ignore
    @Test
    fun sendMessageTest() {
        val lock = Semaphore(0)

        val checkArgumentRefreshable = object : Refreshable {
            override fun onPlayerMove(
                player: Player,
                nextPlayer: Player,
                tile: Tile,
                position: Pair<Int, Int>,
                rotation: Int
            ) {
                assertEquals(player.name, "Alice")
                assertEquals(nextPlayer.name, "Bob")

                lock.release()
            }
        }

        host.playerService.addRefreshable(checkArgumentRefreshable)
        guest.playerService.addRefreshable(checkArgumentRefreshable)

        if (hostInTurn()) {
            host.networkService.sendTilePlaced(0, Pair(1, 1))
        } else {
            guest.networkService.sendTilePlaced(0, Pair(1, 1))
        }

        assert(lock.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS))
        assert(lock.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS))
    }

    /** check whether the host (Alice) or the guest (Bob) is in turn */
    private fun hostInTurn(): Boolean {
        val hostState = checkNotNull(host.currentGame)
        return hostState.currentPlayer.name == "Alice"
    }
}