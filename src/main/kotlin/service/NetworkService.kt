package service

import edu.udo.cs.sopra.ntf.TilePlacedMessage
import entity.PlayerConfig
import kotlin.math.absoluteValue

/**
 * Type abstracting away network interactions required to play Indigo online. The [NetworkService]
 * allows creating and joining games, as well as notifying participating players of actions of
 * the local player. Incoming messages are decoded by the [NetworkService] and delegated to
 * the appropriate components of the service layer, e.g. incoming tile placement messages
 * will cause [PlayerActionService.playerMove] to be called. Because the [NetworkService] delegates
 * all events to other parts of the service layer it does not need to implement [AbstractRefreshingService]
 * itself, all relevant callbacks will be invoked from either [RootService] or [PlayerActionService].
 * @property rootService reference to the root service used for state access and game initialization
 * @property indigoClient object handling the sending and receiving of messages as well as the connection state
 */
class NetworkService(private val rootService: RootService) {
    private var indigoClient: IndigoClient? = null

    /**
     * Create a new game as the host. Once enough players have joined, the [RootService.startGame]
     * method will be called to initialize the game. [Refreshable.onGameStart] will be called on all
     * instances of [Refreshable] registered on the [RootService]. Each joining player will cause
     * the [Refreshable.onPlayerJoinedGame] refresh to be invoked on [PlayerActionService].
     * @param sessionID ID used by all other players to join the game.
     *                  It's best to choose some a string to avoid collisions with other running games.
     * @param config Config of the host player used throughout the session. The current protocol does not support
     *               two players with the same name. Choose carefully.
     * @param gameMode The [entity.GameMode] played in the session. This value dictates how many
     *                 players may join before the game is started.
     * @throws NetworkServiceException when the connection fails or the game cannot be created
     */
    fun createGame(sessionID: String, config: PlayerConfig, gameMode: entity.GameMode) {
        val handler = HostMessageHandler(rootService, config, gameMode)
        val client = IndigoClient(handler, config.name)

        indigoClient = client

        if (client.connect()) {
            client.createGame("Indigo", sessionID, "Hello World :)")
        } else {
            throw NetworkServiceException(NetworkServiceException.Type.CannotConnectToServer)
        }
    }

    /**
     * Join into an existing session. Once the host starts the game, the [entity.GameState] maintained in
     * [RootService] will be set directly and [Refreshable.onGameStart] will be invoked on all instances
     * of [Refreshable] added to the [RootService]. Once joined, the [Refreshable.onPlayerJoinedGame] refresh
     * will be invoked for each player already in the session. It will also be invoked for each player
     * joining after us.
     * @param sessionID ID of the session to join. This value should be chosen at random to avoid collisions.
     *  @param config Config of the host player used throughout the session. The current protocol does not support
     *                two players with the same name. Choose carefully.
     * @throws NetworkServiceException when the connection fails or joining the game fails
     */
    fun joinGame(sessionID: String, config: PlayerConfig) {
        val handler = GuestMessageHandler(rootService, config)
        val client = IndigoClient(handler, config.name)

        indigoClient = client

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
