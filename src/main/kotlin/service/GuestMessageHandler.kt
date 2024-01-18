package service

import edu.udo.cs.sopra.ntf.*
import entity.Tile
import tools.aqua.bgw.net.common.response.JoinGameResponse
import tools.aqua.bgw.net.common.response.JoinGameResponseStatus


/**
 * Class handling bgw-net messages when running as a guest.
 * @property rootService reference to the [RootService] for state access and invoking methods on instances of [Refreshable]
 * @property name Name of the guest. Must be unique in the session.
 */
class GuestMessageHandler(private val rootService: RootService,
                          private val name: String): MessageHandler {
    override fun onJoinGame(client: IndigoClient, resp: JoinGameResponse) {
        if (resp.status != JoinGameResponseStatus.SUCCESS) {
            throw NetworkServiceException(NetworkServiceException.Type.CannotJoinGame)
        }

        println("Successfully joined game")
    }

    override fun onInitMessage(client: IndigoClient, initMessage: GameInitMessage, sender: String) {
        val tiles = initMessage.tileList.map { Tile(translateTileType(it)) }.toMutableList()

        val players = initMessage.players.map {
            val firstTile = tiles.removeFirst()
            val player = translatePlayer(it, name == it.name)

            player.currentTile = firstTile
            player
        }

        val treasureTiles = hashMapOf(
            Pair(Pair(0, 0), Tile(entity.TileType.TREASURE_CENTER))
        )

        Tile.allBorderTreasureTiles.forEach {
            treasureTiles[it.first] = it.second.deepCopy()
        }

        val gates = translateMode(initMessage.gameMode).gateConfiguration()
        val state = entity.GameState(players[0], entity.Board(gates, entity.TileGrid(treasureTiles)), players, tiles)

        setGameState(state)

        rootService.onAllRefreshables { onGameStart(players, gates.toList()) }
    }

    override fun onTilePlaced(client: IndigoClient, tilePlacedMessage: TilePlacedMessage, sender: String) {
        val currentPlayer = getGameState().currentPlayer
        val rotation = tilePlacedMessage.rotation
        val position = Pair(tilePlacedMessage.qcoordinate, tilePlacedMessage.rcoordinate)

        val tile = checkNotNull(currentPlayer.currentTile) {
            "current player does not hold a tile"
        }

        if (CommonMethods.isValidMove(getGameState(), tile, rotation, position)) {
            rootService.playerService.playerMove(Pair(tile, rotation), position)
        } else {
            println("Error: Received invalid move by a remote player")
        }
    }

    private fun getGameState(): entity.GameState = checkNotNull(rootService.currentGame)

    private fun setGameState(state: entity.GameState) {
        rootService.currentGame = state
    }

    private fun translateMode(mode: GameMode): entity.GameMode =
        when(mode) {
            GameMode.TWO_NOT_SHARED_GATEWAYS -> entity.GameMode.TWO_PLAYERS
            GameMode.THREE_SHARED_GATEWAYS -> entity.GameMode.THREE_PLAYERS_SHARED_GATES
            GameMode.THREE_NOT_SHARED_GATEWAYS -> entity.GameMode.THREE_PLAYERS
            GameMode.FOUR_SHARED_GATEWAYS -> entity.GameMode.FOUR_PLAYERS
        }

    private fun translateTileType(tile: TileType): entity.TileType =
        when(tile) {
            TileType.TYPE_0 -> entity.TileType.LONG_CURVES
            TileType.TYPE_1 -> entity.TileType.STRAIGHTS_ONLY
            TileType.TYPE_2 -> entity.TileType.STRAIGHT_NOCROSS
            TileType.TYPE_3 -> entity.TileType.CURVES_TO_CORNER
            TileType.TYPE_4 -> entity.TileType.CORNERS_ONLY
        }

    private fun translatePlayer(player: Player, isSelf: Boolean = false): entity.Player {
        val type = if (isSelf) entity.PlayerType.PERSON else entity.PlayerType.REMOTE
        val token = translateToken(player.color)

        return entity.Player(player.name, 0, type, token)
    }

    private fun translateToken(color: PlayerColor): entity.PlayerToken =
        when (color) {
            PlayerColor.BLUE -> entity.PlayerToken.CYAN
            PlayerColor.PURPLE -> entity.PlayerToken.PURPLE
            PlayerColor.RED -> entity.PlayerToken.RED
            PlayerColor.WHITE -> entity.PlayerToken.WHITE
        }
}