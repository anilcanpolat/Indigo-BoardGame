package service

/**
 * Type wrapping network interactions with remote players.
 * @param rootService reference to the root service used for state access and game initialization
 */
class NetworkService(val rootService: RootService) {
    private var indigoClient: IndigoClient? = null

    /**
     * Create a new game as the host. Once enough players have joined, the [[RootService.startGame]]
     * method will be called to initialize the game.
     * @param sessionID id used by all other players to join the game
     * @param name name of the host player
     * @param gameMode The gamemode played in the session. This will decide how many players can join before starting.
     */
    fun createGame(sessionID: String, name: String, gameMode: entity.GameMode) {
        error("not implemented")
    }

    /**
     * Join into an existing session. Once the host starts the game, the [entity.GameState] maintained in
     * [RootService] will be set directly. Then [NetworkService] will call [Refreshable.onGameStart].
     */
    fun joinGame(sessionID: String, name: String) {
        val handler = GuestMessageHandler(this, name)
        val client = IndigoClient(handler, name)

        if (client.connect()) {
            client.joinGame(sessionID, "Hello, World!")
        } else {
            throw NetworkServiceException(NetworkServiceException.Type.CannotConnectToServer)
        }
    }

    /**
     * Send a message to all players notifying them that a tile has been placed.
     * @param tile the placed tile
     * @param rotation rotation of the tile
     * @param position position of the tile
     */
    fun sendTilePlaced(tile: entity.Tile, rotation: Int, position: Pair<Int, Int>) {
        error("not implemented")
    }
}
