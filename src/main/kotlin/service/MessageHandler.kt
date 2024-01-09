package service

import edu.udo.cs.sopra.ntf.GameInitMessage
import edu.udo.cs.sopra.ntf.TilePlacedMessage

import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.JoinGameResponse

/**
 * Type receiving events received from the bgw backend.
 */
interface MessageHandler {
    /**
     * Method called when a new game was successfully created at the clients request.
     * @param client Reference to the client receiving the response.
     * @param resp Response object send by the backend.
     */
    fun onCreateGame(client: IndigoClient, resp: CreateGameResponse) {}

    /**
     * Method called when the client joined another players game.
     * @param client Reference to the client receiving the response.
     * @param resp Response object send by the backend.
     */
    fun onJoinGame(client: IndigoClient, resp: JoinGameResponse) {}

    /**
     * Method called when another player joins the current game.
     * @param client Reference to the client receiving the response.
     * @param player Object wrapping information about the joined player.
     */
    fun onPlayerJoined(client: IndigoClient, player: PlayerJoinedNotification) {}

    /**
     * Method called when the game is started by the host.
     * @param client Reference to the client receiving the response.
     * @param initMessage Full state of the newly created game. Can be used to fully instantiate [entity.GameState].
     * @param sender Name of the sender.
     */
    fun onInitMessage(client: IndigoClient, initMessage: GameInitMessage, sender: String) {}

    /**
     * Method called when a tile was placed by the current player.
     * The passed state is minimal, but the player executing the
     * action and the placed tile can be derived from it.
     * @param client Reference to the client receiving the response.
     * @param tilePlacedMessage Data send from another member of the session.
     * @param sender Name of the sender.
     */
    fun onTilePlaced(client: IndigoClient, tilePlacedMessage: TilePlacedMessage, sender: String) {}
}