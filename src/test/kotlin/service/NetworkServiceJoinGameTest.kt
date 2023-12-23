package service

import java.util.concurrent.Semaphore
import entity.GameMode
import entity.Player
import entity.PlayerToken
import kotlin.test.*
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

/** test cases for [NetworkService.joinGame] */
class NetworkServiceJoinGameTest {
    /** make sure that [Refreshable.onGameStart] is called in a guest when the host starts the game */
    @Test
    fun joinGameTest() {
        val host = RootService()
        val guest = RootService()

        val sessionID = java.util.Random().nextInt().toString()
        val gameStartSemaphore = Semaphore(0)

        guest.networkService.addRefreshable(object: Refreshable {
            override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
                check(players.any { it.name == "Alice" })
                check(players.any { it.name == "Bob" })

                gameStartSemaphore.release()
            }
        })

        runBlocking {
            host.networkService.createGame(sessionID, "Alice", GameMode.TWO_PLAYERS)
        }

        guest.networkService.joinGame(sessionID, "Bob")

        check(gameStartSemaphore.tryAcquire(5, TimeUnit.SECONDS)) {
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

            guest.networkService.addRefreshable(object: Refreshable {
                override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
                    check(players.any { it.name == "Alice" })
                    check(players.any { it.name == "Bob" })

                    if (mode == GameMode.THREE_PLAYERS || mode == GameMode.THREE_PLAYERS_SHARED_GATES) {
                        check(players.any { it.name == "Charlie" })
                    }

                    if (mode == GameMode.FOUR_PLAYERS) {
                        check(players.any { it.name == "Dave" })
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
                        GameMode.TWO_PLAYERS -> check(numberOwned == 6)
                        GameMode.THREE_PLAYERS -> check(numberOwned == 6)
                        GameMode.THREE_PLAYERS_SHARED_GATES -> check(numberOwned == 3 && numberShared == 3)
                        GameMode.FOUR_PLAYERS -> check(numberShared == 6)
                    }

                    semaphore.release()
                }
            })

            runBlocking {
                host.networkService.createGame(sessionID, "Alice", mode)
            }

            guest.networkService.joinGame(sessionID, "Bob")

            if (mode != GameMode.TWO_PLAYERS) {
                RootService().networkService.joinGame(sessionID, "Charlie")
            }

            if (mode == GameMode.FOUR_PLAYERS) {
                RootService().networkService.joinGame(sessionID, "Dave")
            }

            check(semaphore.tryAcquire(5, TimeUnit.SECONDS)) {
                "waiting for call to onGameStart timed out"
            }
        }
    }

    /**
     * Make sure that [NetworkService.joinGame] constructs a valid [entity.GameState]
     * that is equivalent to the hosts [entity.GameState]
     */
    @Test
    fun gameStateConstruction() {
        val host = RootService()
        val guest = RootService()
        val sessionID = java.util.Random().nextInt().toString()
        val gameStartSemaphore = Semaphore(0)

        runBlocking {
            host.networkService.createGame(sessionID, "Alice", GameMode.TWO_PLAYERS)
        }

        guest.networkService.addRefreshable(object : Refreshable {
            override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
                gameStartSemaphore.release()
            }
        })

        guest.networkService.joinGame(sessionID, "Bob")

        check(gameStartSemaphore.tryAcquire(5, TimeUnit.SECONDS)) {
            "waiting for call to onGameStart timed out"
        }

        val hostState = checkNotNull(host.currentGame)
        val guestState = checkNotNull(guest.currentGame)

        check(hostState.drawPile == guestState.drawPile)
        check(hostState.board.gates.contentEquals(guestState.board.gates))
        check(hostState.board.grid == guestState.board.grid)

        check(hostState.currentPlayer.let { p ->
            guestState.currentPlayer.let { q ->
                p.name == q.name && p.playerToken == q.playerToken && p.currentTile == q.currentTile
            }
        })
    }
}