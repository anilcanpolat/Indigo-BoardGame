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
            val token = tokens.removeFirst()
            val tile = tiles.removeFirst()

            Player(it.name, it.age, it.type, token, tile, useRandomAI = it.useRandomAI, aiDelay = it.aiDelay)
        }

        val treasureTiles = hashMapOf(
            Pair(Pair(0, 0), Tile(TileType.TREASURE_CENTER))
        )

        Tile.allBorderTreasureTiles.forEach {
            treasureTiles[it.first] = it.second.deepCopy()
        }

        val grid = TileGrid(treasureTiles)
        val gates = gameMode.gateConfiguration()
        val board = Board(gates, grid)

        currentGame = GameState(playerList[0], board, playerList, tiles)

        onAllRefreshables { onGameStart(playerList, gates.toList()) }

        if (!isNetworkGame()) {
            playerService.processAllAIMoves()
        }

        val state = checkNotNull(currentGame)

        if (state.currentPlayer.playerType == PlayerType.PERSON) {
            onAllRefreshables { onWaitForInput() }
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
        val state = checkNotNull(currentGame) { "game is not initialised" }
        val prev = state.previousState

        if (!isNetworkGame()) {
            if (prev != null) {
                currentGame = prev

                onAllRefreshables { onStateChange(prev) }
            }
        }
    }

    /**
     * Advance the [GameState] assuming that subsequent [GameState] exists
     */

    fun redo() {
        val state = checkNotNull(currentGame) { "game is not initialised" }
        val next = state.nextState

        if (!isNetworkGame()) {
            if (next != null) {
                currentGame = next

                onAllRefreshables { onStateChange(next) }
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
     * Save the current game state to some instance of [File].
     * @param file file to store the current gamestate in
     */
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    fun save(file: File) {
        val stateList: MutableList<SerializableGameState> = mutableListOf()
        var fstGameState = currentGame

        while (fstGameState?.previousState != null) {
            fstGameState = fstGameState.previousState
        }

        var currentGameIndex = -1
        var currentIndex = 0

        while (fstGameState != null) {
            stateList.add(SerializableGameState(fstGameState))

            if (fstGameState === checkNotNull(currentGame)) {
                currentGameIndex = currentIndex
            }

            fstGameState = fstGameState.nextState
            currentIndex += 1
        }

        val saveState = SaveState(stateList.toList(), currentGameIndex)

        val json = Json { allowStructuredMapKeys = true }
        file.writeText(json.encodeToString(saveState))
    }

    /**
     * Save the current game state to some location on the disk.
     * @param path path where the saved gamestate will be stored
     */
    fun save(path: String) {
        save(File(path))
    }

    /**
     * Restore the gamestate from some instance of [File].
     * @param file file to restore the gamestate from
     */
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    fun load(file: File) {
        val json = Json { allowStructuredMapKeys = true }
        val jsonStr = file.readText()

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

            s.currentPlayer = checkNotNull(s.players.find { player ->
                it.currentPlayer == player
            })
        }

        if (currentGame != null) {
            onAllRefreshables { onStateChange(currentGame!!) }
        }
    }

    /**
     * Load a previously saved game from the disk. This will replace the [currentGame]
     * and call [Refreshable.onStateChange] on all attached [Refreshable] objects.
     * @param path path to the saved game state
     */
    fun load(path: String) {
        load(File(path))
    }
}
