package service

import entity.*
import kotlin.test.*

class IsValidMoveTest {
    private var state: GameState? = null

    @BeforeTest
    fun initGameState() {
        val rootService = RootService()

        val players = listOf(
            PlayerConfig("Alice", -1, PlayerType.PERSON),
            PlayerConfig("Bob", -1, PlayerType.PERSON)
        )

        rootService.startGame(players, GameMode.TWO_PLAYERS)
        state = rootService.currentGame
    }

    @Test
    fun blockingGateTest() {
        var tile = setTile(Tile(TileType.CORNERS_ONLY))

        assertTrue(CommonMethods.isValidMove(gameState(), tile, 0, Pair(1, 3)))
        assertFalse(CommonMethods.isValidMove(gameState(), tile, 1, Pair(1, 3)))

        tile = setTile(Tile(TileType.CORNERS_ONLY))

        assertTrue(CommonMethods.isValidMove(gameState(), tile, 0, Pair(-4, 3)))
        assertFalse(CommonMethods.isValidMove(gameState(), tile, 5, Pair(-4, 3)))
    }

    private fun setTile(tile: Tile): Tile {
        gameState().currentPlayer.currentTile = tile
        return tile
    }

    private fun gameState(): GameState = checkNotNull(state)
}