package service

import edu.udo.cs.sopra.ntf.GameInitMessage
import edu.udo.cs.sopra.ntf.TilePlacedMessage
import entity.*
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.JoinGameResponse

/**
 * Type wrapping network interactions with remote players.
 * @param rootService reference to the root service used for state access and game initialization
 */
class NetworkService(val rootService: RootService): MessageHandler {
    private var state = ConnectionState.DISCONNECTED
    private var client = IndigoClient(this, "dummy")
    private var gameMode = GameMode.TWO_PLAYERS
    private val playerList: MutableList<String> = mutableListOf()
    private var name = "dummy"

    /**
     * Create a new game as the host. Once enough players have joined, the [[RootService.startGame]]
     * method will be called to initialize the game.
     * @param sessionID id used by all other players to join the game
     * @param name name of the host player
     * @param gameMode The gamemode played in the session. This will decide how many players can join before starting.
     */
    fun createGame(sessionID: String, name: String, gameMode: GameMode) {
        check(state == ConnectionState.DISCONNECTED) { "connection is already established" }

        client = IndigoClient(this, name)

        this.name = name
        this.gameMode = gameMode

        if (client.connect()) {
            state = ConnectionState.CONNECTED
            client.createGame("Indigo", sessionID, "Hello, World")
            state = ConnectionState.HOST_WAITING_FOR_CONFIRMATION
        }
    }

    /**
     * Join into an existing session. Once the host starts the game, this will manually initialize the
     * [[GameState]] in the [[RootService]]. The [[NetworkService]] will take care of calling the right
     * [[Refreshable]] callbacks.
     */
    fun joinGame(sessionID: String, name: String) {
        check(state == ConnectionState.DISCONNECTED) { "connection is already established" }

        client = IndigoClient(this, name)

        if (client.connect()) {
            state = ConnectionState.CONNECTED
            client.joinGame(sessionID, "Hello, World")
            state = ConnectionState.GUEST_WAITING_FOR_CONFIRMATION
        }
    }

    /**
     * Send a message to all players notifying them that a tile has been placed.
     * @param tile the placed tile
     * @param rotation rotation of the tile
     * @param position position of the tile
     */
    fun sendTilePlaced(tile: Tile, rotation: Int, position: Pair<Int, Int>) {
        check(false) { "not implemented" }
    }

    override fun onCreateGame(resp: CreateGameResponse) {
        check(state == ConnectionState.HOST_WAITING_FOR_CONFIRMATION) {
            "onCreateGame should only be called in the HOST_WAITING_FOR_CONFIRMATION state"
        }

        state = ConnectionState.WAITING_FOR_GUESTS
    }

    override fun onJoinGame(resp: JoinGameResponse) {
        check(state == ConnectionState.GUEST_WAITING_FOR_CONFIRMATION) {
            "onJoinGame should only be called in the GUEST_WAITING_FOR_CONFIRMATION state"
        }

        state = ConnectionState.WAITING_FOR_INIT
    }

    override fun onPlayerJoined(player: PlayerJoinedNotification) {
        check(state == ConnectionState.WAITING_FOR_GUESTS) {
            "onPlayerJoined should only be called in the WAITING_FOR_GUESTS state"
        }

        playerList.add(player.sender)

        if (playerList.size == playerLimit()) {
            val host = PlayerConfig(name, 42, PlayerType.PERSON)
            val playerList = playerList.map { PlayerConfig(it, 42, PlayerType.REMOTE) }.toMutableList()

            playerList.add(host)
            // rootService.startGame(playerList, gameMode)

            // TODO: convert gamestate to ntf state and send to other players

            playerList.clear()
        }
    }

    override fun onInitMessage(initMessage: GameInitMessage) {
        check(state == ConnectionState.GUEST_WAITING_FOR_CONFIRMATION) {
            "init message can only be received in the GUEST_WAITING_FOR_CONFIRMATION state"
        }

        // TODO: handle init message
    }

    override fun onTilePlaced(tilePlacedMessage: TilePlacedMessage) {
        check(state == ConnectionState.PLAYING_TURN || state == ConnectionState.WAITING_FOR_OPPONENT) {
            "tile placement messages can only be received in the PLAYING_TURN or WAITING_FOR_OPPONENT state"
        }

        // TODO: handle tile placement
    }

    /**
     * Calculate the amount of players supported for the gamemode specified by [[NetworkService.gameMode]]
     * @return integer value between 2 and 4
     */
    private fun playerLimit(): Int {
        return when (gameMode) {
            GameMode.TWO_PLAYERS -> 2
            GameMode.THREE_PLAYERS -> 3
            GameMode.THREE_PLAYERS_SHARED_GATES -> 3
            GameMode.FOUR_PLAYERS -> 4
        }
    }
}