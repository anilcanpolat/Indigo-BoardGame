package service

import java.io.File
import kotlinx.serialization.json.Json
import entity.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

/**
 * Main class of the service layer for the card game. Provides access
 * to all other service classes and holds the [currentGame] state for these
 * services to access.
 */
class RootService : AbstractRefreshingService() {
    var currentGame: GameState? = null
    var networkService = NetworkService(this)
    var playerService = PlayerActionService(this)

    /**
     * Create a new local game. Creating a network game, either as a host or
     * a guest has to be done using the [networkService]. The [networkService]
     * will call [startGame] indirectly, when the network game is started.
     * @param players list of players participating in the game
     * @param gameMode The [GameMode] to play. This must match the size of [players].
     * @throws IllegalStateException when the size of [players] does not match the value of [gameMode]
     */
    fun startGame(players: List<PlayerConfig>, gameMode: GameMode) {
        require(players.size == playerCountForMode(gameMode)) {
            "incorrect GameMode chosen for the number of participating players"
        }

        val tiles = listOf(
            Pair(14, TileType.LONG_CURVES),
            Pair(6, TileType.STRAIGHTS_ONLY),
            Pair(14, TileType.STRAIGHT_NOCROSS),
            Pair(14, TileType.CURVES_TO_CORNER),
            Pair(6, TileType.CORNERS_ONLY)
        ).flatMap { tileConfig ->
            List(tileConfig.first) { tileConfig.second }
        }.map { Tile(it) }.shuffled().toMutableList()

        val tokens = PlayerToken.values().toMutableList()

        val playerList = players.map {
            val token = tokens.removeLast()
            val tile = tiles.removeFirst()

            Player(it.name, it.age, it.type, token, tile)
        }

        val treasureTiles = hashMapOf(
            Pair(Pair(0, 0), Tile(TileType.TREASURE_CENTER)),
            Pair(Pair(-4, 4), Tile(TileType.TREASURE_CORNER)),
            Pair(Pair(0, 4), Tile(TileType.TREASURE_CORNER)),
            Pair(Pair(-4, 0), Tile(TileType.TREASURE_CORNER)),
            Pair(Pair(4, -4), Tile(TileType.TREASURE_CORNER)),
            Pair(Pair(4, 0), Tile(TileType.TREASURE_CORNER)),
            Pair(Pair(0, -4), Tile(TileType.TREASURE_CORNER))
        )

        val grid = TileGrid(treasureTiles)
        val gates = gatesForMode(playerList, gameMode)
        val board = Board(gates, grid)

        currentGame = GameState(playerList[0], board, playerList, tiles)

        onAllRefreshables { onGameStart(playerList, gates.toList()) }
    }

    /**
     * Get the gate configuration for the given game mode.
     * @param players list of players to get their tokens from
     * @param mode [GameMode] used to derive the configuration
     * @return array with 6 items holding two [PlayerToken] objects each
     */
    private fun gatesForMode(players: List<Player>, mode: GameMode): Array<Pair<PlayerToken, PlayerToken>> =
        when (mode) {
            GameMode.TWO_PLAYERS -> {
                val fst = players[0].playerToken
                val snd = players[1].playerToken

                val fstPair = Pair(fst, fst)
                val sndPair = Pair(snd, snd)

                arrayOf(fstPair, sndPair, fstPair, sndPair, fstPair, sndPair)
            }

            GameMode.THREE_PLAYERS -> {
                val fst = players[0].playerToken
                val snd = players[1].playerToken
                val thd = players[2].playerToken

                val fstPair = Pair(fst, fst)
                val sndPair = Pair(snd, snd)
                val thdPair = Pair(thd, thd)

                arrayOf(fstPair, sndPair, thdPair, fstPair, sndPair, thdPair)
            }

            GameMode.THREE_PLAYERS_SHARED_GATES -> {
                val fst = players[0].playerToken
                val snd = players[1].playerToken
                val thd = players[2].playerToken

                arrayOf(Pair(fst, fst), Pair(fst, snd), Pair(thd, thd), Pair(thd, fst), Pair(snd, snd), Pair(snd, thd))
            }

            GameMode.FOUR_PLAYERS -> {
                val fst = players[0].playerToken
                val snd = players[1].playerToken
                val thd = players[2].playerToken
                val fth = players[3].playerToken

                arrayOf(Pair(fst, snd), Pair(snd, thd), Pair(fst, fth), Pair(fth, snd), Pair(thd, fst), Pair(thd, fth))
            }
        }

    /**
     * Get the number of players required for the given [GameMode]
     * @return integer between 2 and 4 (inclusive)
     */
    private fun playerCountForMode(mode: GameMode): Int =
        when (mode) {
            GameMode.TWO_PLAYERS -> 2
            GameMode.THREE_PLAYERS -> 3
            GameMode.THREE_PLAYERS_SHARED_GATES -> 3
            GameMode.FOUR_PLAYERS -> 4
        }

    /**
     * Regress the [GameState] assuming that previous [GameState] exists
     */
    fun undo() {
        checkNotNull(currentGame)
        if (!isNetworkGame()) {
            if (currentGame?.previousState != null) {
                currentGame = currentGame?.previousState
            }
        }
    }

    /**
     * Advance the [GameState] assuming that subsequent [GameState] exists
     */

    fun redo() {
        checkNotNull(currentGame)
        if (!isNetworkGame()) {
            if (currentGame?.nextState != null) {
                currentGame = currentGame?.nextState
            }
        }
    }

    /**
     * Check whether the current game has any network players.
     */
    private fun isNetworkGame() =
        checkNotNull(currentGame).players.any {
            it.playerType == PlayerType.REMOTE
        }


    /**
     * Save the current game state to some location on the disk.
     * @param path path where the saved gamestate will be stored
     */
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    fun save(path: String) {
        val stateList: MutableList<SerializableGameState> = mutableListOf()
        var fstGameState = currentGame

        while (fstGameState?.previousState != null) {
            fstGameState = fstGameState.previousState
        }

        var currentGameIndex = -1
        var currentIndex = 0

        while (fstGameState != null) {
            stateList.add(SerializableGameState(fstGameState))
            fstGameState = fstGameState.nextState

            if (fstGameState === checkNotNull(currentGame)) {
                currentGameIndex = currentIndex
            }

            currentIndex += 1
        }

        val saveState = SaveState(stateList.toList(), currentGameIndex)

        val json = Json { allowStructuredMapKeys = true }
        val file = File(path)

        file.writeText(json.encodeToString(saveState))
    }

    /**
     * Load a previously saved game from the disk. This will replace the [currentGame]
     * and call [Refreshable.onStateChange] on all attached [Refreshable] objects.
     * @param path path to the saved game state
     */
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    fun load(path: String) {
        val json = Json { allowStructuredMapKeys = true }
        val jsonStr = File(path).readText()

        val state: SaveState = json.decodeFromString(jsonStr)
        var newGameState: GameState? = null
        var currentIndex = 0

        state.states.forEach {
            val s = GameState(it.currentPlayer, it.board, it.players, it.drawPile, newGameState)

            if (newGameState != null) {
                newGameState!!.nextState = s
            }

            newGameState = s

            if (currentIndex == state.currentState) {
                currentGame = s
            }

            currentIndex += 1
        }

        if (currentGame != null) {
            onAllRefreshables { onStateChange(currentGame!!) }
        }
    }

}
