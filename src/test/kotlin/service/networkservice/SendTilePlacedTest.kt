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

        host.networkService.createGame(sessionID, NetworkConfig.ALICE, GameMode.TWO_PLAYERS)

        Thread.sleep(NetworkConfig.TEST_TIMEOUT)

        guest.addRefreshable(object : Refreshable {
            override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
                lock.release()
            }
        })

        guest.networkService.joinGame(sessionID, NetworkConfig.BOB)

        check(lock.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS)) {
            "waiting for call to onGameStart timed out"
        }
    }

    /**
     * Make sure that calling [service.NetworkService.sendTilePlaced] causes [Refreshable.onPlayerMove]
     * to be called on all participating players.
     */
    @Test
    fun sendMessageTest() {
        val lock = Semaphore(0)
        var firstCallHost = true
        var firstCallGuest = true

        host.playerService.addRefreshable(object : Refreshable {
            override fun onPlayerMove(
                player: Player,
                nextPlayer: Player,
                tile: Tile,
                position: Pair<Int, Int>,
                rotation: Int
            ) {
                if (firstCallHost) {
                    assertEquals(player.name, "Alice")
                    assertEquals(nextPlayer.name, "Bob")
                    firstCallHost = false
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
                if (firstCallGuest) {
                    assertEquals(player.name, "Alice")
                    assertEquals(nextPlayer.name, "Bob")
                    firstCallGuest = false
                } else {
                    assertEquals(player.name, "Bob")
                    assertEquals(nextPlayer.name, "Alice")
                }

                lock.release()
            }
        })

        val hostTile = checkNotNull(checkNotNull(host.currentGame).currentPlayer.currentTile)
        host.playerService.playerMove(Pair(hostTile, 0), Pair(1, 0))

        assertTrue(lock.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS))
        assertTrue(lock.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS))

        val guestTile = checkNotNull(checkNotNull(guest.currentGame).currentPlayer.currentTile)
        guest.playerService.playerMove(Pair(guestTile, 0), Pair(-1, 0))

        assertTrue(lock.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS))
        assertTrue(lock.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS))
    }
}