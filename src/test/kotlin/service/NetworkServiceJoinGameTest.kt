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

    /** make sure that the gate configuration created by [GuestMessageHandler.gatesFromMode] is correct */
    @Test
    fun gateConfigTest() {
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

            host.networkService.createGame(sessionID, "Alice", mode)
            Thread.sleep(1000)

            guest.networkService.joinGame(sessionID, "Bob")

            if (mode != GameMode.TWO_PLAYERS) {
                RootService().networkService.joinGame(sessionID, "Charlie")
            }

            if (mode == GameMode.FOUR_PLAYERS) {
                RootService().networkService.joinGame(sessionID, "Dave")
            }

            semaphore.acquire()
        }
    }
}