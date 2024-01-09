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

        guest.addRefreshable(object : Refreshable {
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
     * to be called on all participating players. This test currently relies on a partial implementation
     * of [service.PlayerActionService.playerMove] and the [service.TilePlacedMessage] workaround.
     */
    @Test
    fun sendMessageTest() {
        val lock = Semaphore(0)
        var firstCall = true

        host.playerService.addRefreshable(object : Refreshable {
            override fun onPlayerMove(
                player: Player,
                nextPlayer: Player,
                tile: Tile,
                position: Pair<Int, Int>,
                rotation: Int
            ) {
                if (firstCall) {
                    assertEquals(player.name, "Alice")
                    assertEquals(nextPlayer.name, "Bob")
                    firstCall = false
                } else {
                    assertEquals(player.name, "Bob")
                    assertEquals(nextPlayer.name, "Alice")
                }

                lock.release()
            }
        })

        guest.playerService.addRefreshable(object : Refreshable {
            override fun onPlayerMove(
                player: Player,
                nextPlayer: Player,
                tile: Tile,
                position: Pair<Int, Int>,
                rotation: Int
            ) {
                if (firstCall) {
                    assertEquals(player.name, "Bob")
                    assertEquals(nextPlayer.name, "Alice")
                    firstCall = false
                } else {
                    assertEquals(player.name, "Alice")
                    assertEquals(nextPlayer.name, "Bob")
                }

                lock.release()
            }
        })

        if (hostInTurn()) {
            val tile = checkNotNull(checkNotNull(host.currentGame).currentPlayer.currentTile)

            host.playerService.playerMove(Pair(tile, 0), Pair(0, 1))
            host.networkService.sendTilePlaced(0, Pair(0, 1))

            assert(lock.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS))

            guest.networkService.sendTilePlaced(0, Pair(1, 0))
            assert(lock.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS))
        } else {
            val tile = checkNotNull(checkNotNull(guest.currentGame).currentPlayer.currentTile)

            guest.playerService.playerMove(Pair(tile, 0), Pair(0, 1))
            guest.networkService.sendTilePlaced(0, Pair(0, 1))

            assert(lock.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS))

            host.networkService.sendTilePlaced(0, Pair(1, 0))
            assert(lock.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS))
        }
    }

    /** check whether the host (Alice) or the guest (Bob) is in turn */
    private fun hostInTurn(): Boolean {
        val hostState = checkNotNull(host.currentGame)
        return hostState.currentPlayer.name == "Alice"
    }
}