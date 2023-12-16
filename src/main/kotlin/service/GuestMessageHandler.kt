package service

import edu.udo.cs.sopra.ntf.*
import tools.aqua.bgw.net.common.response.JoinGameResponse
import tools.aqua.bgw.net.common.response.JoinGameResponseStatus


/** class handling bgw-net messages when running as a guest */
class GuestMessageHandler(private val networkService: NetworkService,
                          private val name: String): MessageHandler, AbstractRefreshingService() {
    override fun onJoinGame(resp: JoinGameResponse) {
        if (resp.status != JoinGameResponseStatus.SUCCESS) {
            throw NetworkServiceException(NetworkServiceException.Type.CannotJoinGame)
        }

        println("Successfully joined game")
    }

    override fun onInitMessage(initMessage: GameInitMessage, sender: String) {
        val tiles = initMessage.tileList.map { entity.Tile(translateTileType(it)) }.toMutableList()

        val players = initMessage.players.map {
            val firstTile = tiles.removeFirst()
            val player = translatePlayer(it, name == it.name)

            player.currentTile = firstTile
            player
        }

        val gates = gatesFromMode(players, initMessage.gameMode).toTypedArray()
        val state = entity.GameState(players[0], entity.Board(gates, entity.TileGrid(HashMap())), players, tiles)

        setGameState(state)

        onAllRefreshables { onGameStart(players, gates.toList()) }
    }

    override fun onTilePlaced(tilePlacedMessage: TilePlacedMessage, sender: String) {
        val rotation = tilePlacedMessage.rotation
        val position = Pair(tilePlacedMessage.qCoordinate, tilePlacedMessage.rCoordinate)

        networkService.rootService.playerService.playerMove(getGameState().currentPlayer, rotation, position)
    }

    private fun getGameState(): entity.GameState = checkNotNull(networkService.rootService.currentGame)

    private fun setGameState(state: entity.GameState) {
        networkService.rootService.currentGame = state
    }

    private fun gatesFromMode(
        players: List<entity.Player>,
        mode: GameMode
    ): List<Pair<entity.PlayerToken, entity.PlayerToken>> =
        when (mode) {
            GameMode.TWO_NOT_SHARED_GATEWAYS -> {
                val fst = players[0].playerToken
                val snd = players[1].playerToken

                val fstPair = Pair(fst, fst)
                val sndPair = Pair(snd, snd)

                listOf(fstPair, sndPair, fstPair, sndPair, fstPair, sndPair)
            }
            GameMode.THREE_NOT_SHARED_GATEWAYS -> {
                val fst = players[0].playerToken
                val snd = players[1].playerToken
                val thd = players[2].playerToken

                val fstPair = Pair(fst, fst)
                val sndPair = Pair(snd, snd)
                val thdPair = Pair(thd, thd)

                listOf(fstPair, sndPair, thdPair, fstPair, sndPair, thdPair)
            }
            GameMode.THREE_SHARED_GATEWAYS -> {
                val fst = players[0].playerToken
                val snd = players[1].playerToken
                val thd = players[2].playerToken

                listOf(Pair(fst, fst), Pair(fst, snd), Pair(thd, thd), Pair(thd, fst), Pair(snd, snd), Pair(snd, thd))
            }
            GameMode.FOUR_SHARED_GATEWAYS -> {
                val fst = players[0].playerToken
                val snd = players[1].playerToken
                val thd = players[2].playerToken
                val fth = players[3].playerToken

                listOf(Pair(fst, snd), Pair(snd, thd), Pair(fst, fth), Pair(fth, snd), Pair(thd, fst), Pair(thd, fth))
            }
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