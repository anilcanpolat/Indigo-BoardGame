package service

import edu.udo.cs.sopra.ntf.*
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.CreateGameResponseStatus

class HostMessageHandler(private val networkService: NetworkService, private val name: String, private val mode: entity.GameMode): MessageHandler, AbstractRefreshingService() {
    private val playerList = mutableListOf(entity.PlayerConfig(name, 0, entity.PlayerType.PERSON))

    override fun onCreateGame(resp: CreateGameResponse) {
        if (resp.status != CreateGameResponseStatus.SUCCESS) {
            throw NetworkServiceException(NetworkServiceException.Type.CannotCreateGame)
        }
    }

    override fun onPlayerJoined(player: PlayerJoinedNotification) {
        playerList.add(entity.PlayerConfig(player.sender, 0, entity.PlayerType.REMOTE))

        if (playerList.size == playerCountInMode(mode)) {
            networkService.rootService.startGame(playerList, mode)

            val msg = convertGameState()
            networkService.indigoClient!!.sendGameActionMessage(msg)
        }
    }

    override fun onTilePlaced(tilePlacedMessage: TilePlacedMessage) {
        val tile = getGameState().currentPlayer.currentTile!!
        getGameState().currentPlayer.currentTile = null

        val rotation = tilePlacedMessage.rotation
        val position = Pair(tilePlacedMessage.qCoordinate, tilePlacedMessage.rCoordinate)

        // TODO: the service layer is not implemented to the necessary level yet
        // networkService.rootService.playerService.playerMove(Pair(tile, rotation), position)
    }

    private fun getGameState(): entity.GameState = checkNotNull(networkService.rootService.currentGame)

    private fun convertGameState(): GameInitMessage {
        val state = getGameState()

        val firstTiles = state.players.map { it.currentTile!! }
        val otherTiles = state.drawPile

        val tiles = (firstTiles + otherTiles).map {
            convertTileType(it.tileType)
        }

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