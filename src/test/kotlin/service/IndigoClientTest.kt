package service

import kotlin.test.*
import java.util.concurrent.Semaphore

import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.CreateGameResponseStatus
import tools.aqua.bgw.net.common.response.JoinGameResponse

class IndigoClientTest {
    @Test
    fun createGameTest() {
        var createGameCalled = false
        val semaphore = Semaphore(0)

        val client = IndigoClient(object: MessageHandler {
            override fun onCreateGame(resp: CreateGameResponse) {
                assert(resp.status == CreateGameResponseStatus.SUCCESS)
                createGameCalled = true
                semaphore.release()
            }
        }, "Alice")

        check(client.connect()) { "cannot connect to bgw server" }
        client.createGame("Indigo", "Hello World")

        semaphore.acquire()
        assert(createGameCalled)
    }

    @Test
    fun joinGameTest() {
        val hostSemaphore = Semaphore(0)
        val guestSemaphore = Semaphore(0)

        var playerJoinedCalled = false
        var onJoinGameCalled = false

        val sessionID = java.util.Random().nextInt().toString()

        val host = IndigoClient(object: MessageHandler {
            override fun onCreateGame(resp: CreateGameResponse) {
                hostSemaphore.release()
            }

            override fun onPlayerJoined(player: PlayerJoinedNotification) {
                check(player.sender == "Bob")
                playerJoinedCalled = true
                hostSemaphore.release()
            }
        }, "Alice")

        val guest = IndigoClient(object: MessageHandler {
            override fun onJoinGame(resp: JoinGameResponse) {
                onJoinGameCalled = true
                guestSemaphore.release()
            }
        }, "Bob")

        check(host.connect()) { "cannot connect to bgw server" }
        check(guest.connect()) { "cannot connect to bgw server"}

        host.createGame("Indigo", sessionID, "Hello World")
        hostSemaphore.acquire()

        guest.joinGame(sessionID, "Hello World")

        hostSemaphore.acquire()
        guestSemaphore.acquire()

        check(playerJoinedCalled)
        check(onJoinGameCalled)
    }
}