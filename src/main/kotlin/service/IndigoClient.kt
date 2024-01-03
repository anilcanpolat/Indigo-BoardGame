package service

import edu.udo.cs.sopra.ntf.GameInitMessage
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
 */
class IndigoClient(
    private val eventHandler: MessageHandler,
    playerName: String,
    host: String = "sopra.cs.tu-dortmund.de:80/bgw-net/connect",
    secret: String = "game23d"
): BoardGameClient(playerName, host, secret, NetworkLogging.VERBOSE) {
    override fun onCreateGameResponse(response: CreateGameResponse) {
        runOnGUIThread(Runnable {
            eventHandler.onCreateGame(response)
        })
    }

    override fun onJoinGameResponse(response: JoinGameResponse) {
        runOnGUIThread(Runnable {
            eventHandler.onJoinGame(response)
        })
    }

    override fun onPlayerJoined(notification: PlayerJoinedNotification) {
        runOnGUIThread(Runnable {
            eventHandler.onPlayerJoined(notification)
        })
    }

    @GameActionReceiver
    @SuppressWarnings("unused")
    private fun onGameInitMessage(msg: GameInitMessage, sender: String) {
        runOnGUIThread(Runnable {
            eventHandler.onInitMessage(msg, sender)
        })
    }

    @GameActionReceiver
    @SuppressWarnings("unused")
    private fun onTilePlacedMessage(msg: TilePlacedMessage, sender: String) {
        runOnGUIThread(Runnable {
            eventHandler.onTilePlaced(msg, sender)
        })
    }
}
