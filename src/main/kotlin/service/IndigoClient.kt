package service

import edu.udo.cs.sopra.ntf.GameInitMessage
import edu.udo.cs.sopra.ntf.TilePlacedMessage
import tools.aqua.bgw.net.client.BoardGameClient
import tools.aqua.bgw.net.client.NetworkLogging
import tools.aqua.bgw.net.common.annotations.GameActionReceiver
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.JoinGameResponse

/**
 * Network client receiving responses from the bgw backend.
 * Callbacks are forwarded to an instance of [[MessageHandler]]
 */
class IndigoClient(
    private val eventHandler: MessageHandler,
    playerName: String,
    host: String = "sopra.cs.tu-dortmund.de:80/bgw-net/connect",
    secret: String = "game23d"
): BoardGameClient(playerName, host, secret, NetworkLogging.VERBOSE) {
    override fun onCreateGameResponse(response: CreateGameResponse) {
        eventHandler.onCreateGame(response)
    }

    override fun onJoinGameResponse(response: JoinGameResponse) {
        eventHandler.onJoinGame(response)
    }

    override fun onPlayerJoined(notification: PlayerJoinedNotification) {
        eventHandler.onPlayerJoined(notification)
    }

    @GameActionReceiver
    private fun onGameInitMessage(msg: GameInitMessage) {
        eventHandler.onInitMessage(msg)
    }

    @GameActionReceiver
    private fun onTilePlacedMessage(msg: TilePlacedMessage) {
        eventHandler.onTilePlaced(msg)
    }
}
