package service

import edu.udo.cs.sopra.ntf.GameInitMessage
import edu.udo.cs.sopra.ntf.TilePlacedMessage

import tools.aqua.bgw.net.client.BoardGameClient
import tools.aqua.bgw.net.client.NetworkLogging
import tools.aqua.bgw.net.common.annotations.GameActionReceiver
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.JoinGameResponse
import tools.aqua.bgw.core.BoardGameApplication.Companion.runOnGUIThread

/**
 * Network client asynchronously receiving responses from the bgw backend.
 * Callbacks are forwarded to an instance of [[MessageHandler]] and only
 * ever run in the current GUI thread, making it safe to change GUI state
 * in response to any of the callbacks in [[MessageHandler]].
 *
 * The [IndigoClient] offers the option to capture and log all exceptions
 * thrown by function in [MessageHandler]. This option should only be
 * enabled in production builds when expecting some errors to occur
 * with the assumption that those errors are not significant enough
 * to actually halt the process.
 * @property eventHandler object to forward all responses to
 * @property captureCallbackFailures Enables exception capturing. Must be false when testing.
 */
class IndigoClient(
    private val eventHandler: MessageHandler,
    playerName: String,
    host: String = "sopra.cs.tu-dortmund.de:80/bgw-net/connect",
    secret: String = "game23d",
    private val captureCallbackFailures: Boolean = false
): BoardGameClient(playerName, host, secret, NetworkLogging.VERBOSE) {
    override fun onCreateGameResponse(response: CreateGameResponse) {
        runCallback {
            eventHandler.onCreateGame(this, response)
        }
    }

    override fun onJoinGameResponse(response: JoinGameResponse) {
        runCallback {
            eventHandler.onJoinGame(this, response)
        }
    }

    override fun onPlayerJoined(notification: PlayerJoinedNotification) {
        runCallback {
            eventHandler.onPlayerJoined(this, notification)
        }
    }

    @GameActionReceiver
    @SuppressWarnings("unused")
    private fun onGameInitMessage(msg: GameInitMessage, sender: String) {
        runCallback {
            eventHandler.onInitMessage(this, msg, sender)
        }
    }

    @GameActionReceiver
    @SuppressWarnings("unused")
    private fun onTilePlacedMessage(msg: TilePlacedMessage, sender: String) {
        runCallback {
            eventHandler.onTilePlaced(this, msg, sender)
        }
    }

    /**
     * Run some function in the current BGW-GUI thread. Exceptions thrown
     * by the callback function will be captures and logged if the
     * [captureCallbackFailures] property is set to true.
     */
    private fun runCallback(callback: () -> Unit) {
        runOnGUIThread(Runnable {
            if (captureCallbackFailures) {
                try {
                    callback()
                } catch (e: IllegalStateException) {
                    println("exception in event handler: ${e.message}")
                    println(e.stackTraceToString())
                }
            } else {
                callback()
            }
        })
    }
}
