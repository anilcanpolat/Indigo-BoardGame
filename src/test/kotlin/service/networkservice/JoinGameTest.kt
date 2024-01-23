package service.networkservice

import entity.GameMode
import entity.Player
import entity.PlayerToken

import service.Refreshable
import service.RootService

import kotlin.test.*

import java.util.concurrent.TimeUnit
import java.util.concurrent.Semaphore


/** test cases for [service.NetworkService.joinGame] */
class JoinGameTest {
    /** make sure that [Refreshable.onGameStart] is called in a guest when the host starts the game */
    @Test
    fun testOnGameStartCalled() {
        val host = RootService()
        val guest = RootService()

        val sessionID = java.util.Random().nextInt().toString()
        val gameStartSemaphore = Semaphore(0)

        guest.addRefreshable(object: Refreshable {
            override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
                assert(players.any { it.name == "Alice" }) { "player alice missing" }
                assert(players.any { it.name == "Bob" }) { "player bob missing" }

                gameStartSemaphore.release()
            }
        })

        host.networkService.createGame(sessionID, NetworkConfig.ALICE, GameMode.TWO_PLAYERS)
        Thread.sleep(NetworkConfig.TEST_TIMEOUT)

        guest.networkService.joinGame(sessionID, NetworkConfig.BOB)

        assert(gameStartSemaphore.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS)) {
            "waiting for call to onGameStart timed out"
        }
    }

    /** make sure that starting and joining a game with all [GameMode] instances works correctly */
    @Test
    fun joinAllGameModes() {
        val modes = GameMode.values()

        modes.forEach { mode ->
            val host = RootService()
            val guest = RootService()
            val sessionID = java.util.Random().nextInt().toString()
            val semaphore = Semaphore(0)

            guest.addRefreshable(object: Refreshable {
                override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
                    assert(players.any { it.name == "Alice" }) { "player alice missing" }
                    assert(players.any { it.name == "Bob" }) { "player bob missing" }

                    if (mode == GameMode.THREE_PLAYERS || mode == GameMode.THREE_PLAYERS_SHARED_GATES) {
                        assert(players.any { it.name == "Charlie" }) { "player charlie missing" }
                    }

                    if (mode == GameMode.FOUR_PLAYERS) {
                        assert(players.any { it.name == "Dave" }) { "player dave missing" }
                    }

                    var numberShared = 0
                    var numberOwned = 0

                    gates.forEach {
                        if (it.first == it.second) {
                            numberOwned += 1
                        } else {
                            numberShared += 1
                        }
                    }

                    when (mode) {
                        GameMode.TWO_PLAYERS -> assertEquals(numberOwned, 6)
                        GameMode.THREE_PLAYERS -> assertEquals(numberOwned, 6)
                        GameMode.THREE_PLAYERS_SHARED_GATES -> assertEquals(Pair(numberOwned, numberShared), Pair(3, 3))
                        GameMode.FOUR_PLAYERS -> assertEquals(numberShared, 6)
                    }

                    semaphore.release()
                }
            })

            host.networkService.createGame(sessionID, NetworkConfig.ALICE, mode)
            Thread.sleep(NetworkConfig.TEST_TIMEOUT)

            guest.networkService.joinGame(sessionID, NetworkConfig.BOB)

            if (mode != GameMode.TWO_PLAYERS) {
                RootService().networkService.joinGame(sessionID, NetworkConfig.CHARLIE)
            }

            if (mode == GameMode.FOUR_PLAYERS) {
                RootService().networkService.joinGame(sessionID, NetworkConfig.DAVE)
            }

            assert(semaphore.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS)) {
                "waiting for call to onGameStart timed out"
            }
        }
    }

    /**
     * Make sure that [service.NetworkService.joinGame] constructs a valid [entity.GameState]
     * that is equivalent to the hosts [entity.GameState]
     */
    @Test
    fun gameStateConstruction() {
        val host = RootService()
        val guest = RootService()
        val sessionID = java.util.Random().nextInt().toString()
        val gameStartSemaphore = Semaphore(0)

        host.networkService.createGame(sessionID, NetworkConfig.ALICE, GameMode.TWO_PLAYERS)
        Thread.sleep(NetworkConfig.TEST_TIMEOUT)

        guest.addRefreshable(object : Refreshable {
            override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
                gameStartSemaphore.release()
            }
        })

        guest.networkService.joinGame(sessionID, NetworkConfig.BOB)

        assert(gameStartSemaphore.tryAcquire(NetworkConfig.TEST_TIMEOUT, TimeUnit.MILLISECONDS)) {
            "waiting for call to onGameStart timed out"
        }

        val hostState = checkNotNull(host.currentGame)
        val guestState = checkNotNull(guest.currentGame)

        assertEquals(hostState.drawPile, guestState.drawPile)
        assertContentEquals(hostState.board.gates, guestState.board.gates)
        assertEquals(hostState.board.grid, guestState.board.grid)

        assert(hostState.currentPlayer.let { p ->
            guestState.currentPlayer.let { q ->
                p.name == q.name && p.playerToken == q.playerToken && p.currentTile == q.currentTile
            }
        }) { "constructed players differ" }
    }
}