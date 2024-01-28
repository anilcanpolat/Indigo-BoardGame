package service

import edu.udo.cs.sopra.ntf.*
import entity.PlayerConfig
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.CreateGameResponseStatus

/**
 * Class handling bgw-net messages when running as the host.
 * @property rootService Reference to the [RootService] to access state and callback functionality.
 * @property config Config of the host player. Names must be unique in the entire session.
 * @property mode The [GameMode] to host. Dictates how many players may join the session.
 */
class HostMessageHandler(private val rootService: RootService,
                         private val config: PlayerConfig,
                         private val mode: entity.GameMode): MessageHandler {
    private val playerList = mutableListOf(config)

    override fun onCreateGame(client: IndigoClient, resp: CreateGameResponse) {
        if (resp.status != CreateGameResponseStatus.SUCCESS) {
            throw NetworkServiceException(NetworkServiceException.Type.CannotCreateGame)
        }
    }

    override fun onPlayerJoined(client: IndigoClient, player: PlayerJoinedNotification) {
        val playerConfig = PlayerConfig(player.sender, 0, entity.PlayerType.REMOTE)

        playerList.add(playerConfig)
        rootService.playerService.onAllRefreshables { onPlayerJoinedGame(playerConfig) }

        if (playerList.size == playerCountInMode(mode)) {
            rootService.startGame(playerList, mode)

            val msg = convertGameState()
            client.sendGameActionMessage(msg)

            rootService.playerService.processAllAIMoves()
        }
    }

    override fun onTilePlaced(client: IndigoClient, tilePlacedMessage: TilePlacedMessage, sender: String) {
        val rotation = tilePlacedMessage.rotation
        val position = Pair(tilePlacedMessage.qcoordinate, tilePlacedMessage.rcoordinate)
        val currentPlayer = getGameState().currentPlayer

        val tile = checkNotNull(currentPlayer.currentTile) {
            "the current player does not hold a tile"
        }

        if (CommonMethods.isValidMove(getGameState(), tile, rotation, position)) {
            rootService.playerService.playerMove(Pair(tile, rotation), position)
        } else {
            println("Error: Received invalid move by a remote player")
        }
    }

    private fun getGameState(): entity.GameState = checkNotNull(rootService.currentGame)

    private fun convertGameState(): GameInitMessage {
        val state = getGameState()

        val firstTiles = state.players.map { it.currentTile!! }
        val otherTiles = state.drawPile

        val tiles = (firstTiles + otherTiles).map {
            convertTileType(it.tileType)
        }.reversed()

        val players = state.players.map {
            Player(it.name, convertPlayerToken(it.playerToken))
        }

        val mode = convertGameMode(mode)
        return GameInitMessage(players, mode, tiles)
    }

    private fun convertGameMode(mode: entity.GameMode): GameMode =
        when(mode) {
            entity.GameMode.TWO_PLAYERS -> GameMode.TWO_NOT_SHARED_GATEWAYS
            entity.GameMode.THREE_PLAYERS -> GameMode.THREE_NOT_SHARED_GATEWAYS
            entity.GameMode.THREE_PLAYERS_SHARED_GATES -> GameMode.THREE_SHARED_GATEWAYS
            entity.GameMode.FOUR_PLAYERS -> GameMode.FOUR_SHARED_GATEWAYS
        }

    private fun convertPlayerToken(token: entity.PlayerToken): PlayerColor =
        when(token) {
            entity.PlayerToken.RED -> PlayerColor.RED
            entity.PlayerToken.CYAN -> PlayerColor.BLUE
            entity.PlayerToken.PURPLE -> PlayerColor.PURPLE
            entity.PlayerToken.WHITE -> PlayerColor.WHITE
        }

    private fun convertTileType(type: entity.TileType): TileType =
        when(type.toType()) {
            0 -> TileType.TYPE_0
            1 -> TileType.TYPE_1
            2 -> TileType.TYPE_2
            3 -> TileType.TYPE_3
            4 -> TileType.TYPE_4
            else -> error("treasure tiles cannot be converted")
        }

    private fun playerCountInMode(gameMode: entity.GameMode): Int =
        when (gameMode) {
            entity.GameMode.TWO_PLAYERS -> 2
            entity.GameMode.THREE_PLAYERS -> 3
            entity.GameMode.THREE_PLAYERS_SHARED_GATES -> 3
            entity.GameMode.FOUR_PLAYERS -> 4
        }
}
