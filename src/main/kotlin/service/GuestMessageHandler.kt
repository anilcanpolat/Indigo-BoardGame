package service

import edu.udo.cs.sopra.ntf.*
import tools.aqua.bgw.net.common.response.JoinGameResponse

class GuestMessageHandler(val networkService: NetworkService, private val name: String): MessageHandler, AbstractRefreshingService() {
    override fun onJoinGame(resp: JoinGameResponse) {
        println("Successfully joined game")
    }

    override fun onInitMessage(initMessage: GameInitMessage) {
        val tiles = initMessage.tileList.map { entity.Tile(translateTileType(it)) }.toMutableList()

        val players = initMessage.players.map {
            val firstTile = tiles.removeFirst()
            val player = translatePlayer(it, name == it.name) // TODO: check with ntf whether names are unique

            player.currentTile = firstTile
            player
        }

        val gates = gatesFromMode(players, initMessage.gameMode).toTypedArray()
        val state = entity.GameState(players[0], entity.Board(gates, entity.TileGrid(HashMap())), players, tiles)

        setGameState(state)

        onAllRefreshables { onGameStart(players, gates.toList()) }
    }

    override fun onTilePlaced(tilePlacedMessage: TilePlacedMessage) {
        val tile = getGameState().currentPlayer.currentTile!!
        getGameState().currentPlayer.currentTile = null

        val rotation = tilePlacedMessage.rotation
        val position = Pair(tilePlacedMessage.qCoordinate, tilePlacedMessage.rCoordinate)

        // TODO: the service layer is not implemented to the necessary level yet
        // networkService.rootService.playerService.playerMove(Pair(tile, rotation), position)
    }

    // TODO: the service layer is not implemented to the necessary level yet
    private fun getGameState(): entity.GameState = error("not implemented")

    // TODO: the service layer is not implemented to the necessary level yet
    private fun setGameState(state: entity.GameState) {
        error("not implemented")
    }

    private fun gatesFromMode(players: List<entity.Player>, mode: GameMode): List<Pair<entity.PlayerToken, entity.PlayerToken>> =
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