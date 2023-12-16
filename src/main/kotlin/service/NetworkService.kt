package service

import edu.udo.cs.sopra.ntf.TilePlacedMessage
import kotlin.math.absoluteValue

/**
 * Type wrapping network interactions with remote players.
 * @param rootService reference to the root service used for state access and game initialization
 */
class NetworkService(val rootService: RootService) {
    var indigoClient: IndigoClient? = null

    /**
     * Create a new game as the host. Once enough players have joined, the [[RootService.startGame]]
     * method will be called to initialize the game.
     * @param sessionID id used by all other players to join the game
     * @param name name of the host player
     * @param gameMode The gamemode played in the session. This will decide how many players can join before starting.
     */
    fun createGame(sessionID: String, name: String, gameMode: entity.GameMode) {
        val handler = HostMessageHandler(this, name, gameMode)
        indigoClient = IndigoClient(handler, name)

        val client = checkNotNull(indigoClient)

        if (client.connect()) {
            client.createGame("Indigo", sessionID, "Hello World :)")
        } else {
            throw NetworkServiceException(NetworkServiceException.Type.CannotConnectToServer)
        }
    }

    /**
     * Join into an existing session. Once the host starts the game, the [entity.GameState] maintained in
     * [RootService] will be set directly. Then [NetworkService] will call [Refreshable.onGameStart].
     * @param sessionID id of the session to join
     * @param name name the player will use throughout the session
     * @throws NetworkServiceException when the connection fails or joining the game fails
     */
    fun joinGame(sessionID: String, name: String) {
        val handler = GuestMessageHandler(this, name)
        indigoClient = IndigoClient(handler, name)

        val client = checkNotNull(indigoClient)

        if (client.connect()) {
            client.joinGame(sessionID, "Hello World")
        } else {
            throw NetworkServiceException(NetworkServiceException.Type.CannotConnectToServer)
        }
    }

    /**
     * Send a message to all players notifying them that the current player
     * placed their tile at the given position with the given rotation.
     * @param rotation rotation of the tile
     * @param position position of the tile
     * @throws IllegalStateException when the network client has not been initialised by [createGame] or [joinGame] yet
     * @throws IllegalArgumentException when the value for [position] or [rotation] is invalid
     */
    fun sendTilePlaced(rotation: Int, position: Pair<Int, Int>) {
        require(isPositionValid(position)) { "not a valid position for a tile to be placed on" }
        require(rotation in 0..5) { "not a valid value for rotation" }

        val client = checkNotNull(indigoClient) {
            "createGame or joinGame have to be called before sendTilePlaced"
        }

        val message = TilePlacedMessage(rotation, position.first, position.second)
        client.sendGameActionMessage(message)
    }

    /**
     * Check whether a given position is a valid position for a route tile to be placed upon.
     * A valid route tile position has a maximum distance to the center of 4 and is not
     * the position of a treasure tile.
     * This method does not check whether another tile has already been placed there.
     * @param position position to check
     */
    private fun isPositionValid(position: Pair<Int, Int>): Boolean {
        val sCoordinate = - position.first - position.second
        val coords = listOf(position.first, position.second, sCoordinate)
        val distanceToCenter = coords.maxOfOrNull { it.absoluteValue }!!

        // maximum distance of 4 and not the center treasure tile
        if ((distanceToCenter > 4 || distanceToCenter == 0)) {
            return false
        }

        // all edge treasure tiles have a distance of 4 and have exactly one zero value in coords
        if (distanceToCenter == 4 && coords.contains(0)) {
            return false
        }

        return true
    }
}
