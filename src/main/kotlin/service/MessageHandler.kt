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
     */
    fun onCreateGame(resp: CreateGameResponse) {}

    /**
     * Method called when the client joined another players game.
     */
    fun onJoinGame(resp: JoinGameResponse) {}

    /**
     * Method called when another player joins the current game.
     */
    fun onPlayerJoined(player: PlayerJoinedNotification) {}

    /**
     * Method called when the game is started by the current host.
     * This method contains all the state required to fully initialize [[entity.GameState]]
     */
    fun onInitMessage(initMessage: GameInitMessage) {}

    /**
     * Method called when a tile was placed by the current player.
     * The passed state is minimal, but the player executing the
     * action and the placed tile can be derived from it.
     */
    fun onTilePlaced(tilePlacedMessage: TilePlacedMessage) {}
}