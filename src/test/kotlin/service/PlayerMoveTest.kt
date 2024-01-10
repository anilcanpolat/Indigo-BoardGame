package service

import entity.*
import kotlin.test.*

/**
 * Test cases for [PlayerActionService.playerMove]
 */
class PlayerMoveTest {
    private var rootService = RootService()
    private var playerService = rootService.playerService

    /**
     * Reset the old state of [rootService]. Start a new game with two players
     * named Alice and Bob holding each a tile of the type [TileType.STRAIGHTS_ONLY].
     * The draw pile will only hold tiles of the type [TileType.STRAIGHTS_ONLY].
     */
    @BeforeTest
    fun resetGameState() {
        rootService = RootService()
        playerService = rootService.playerService

        val playerConfig = listOf(
            PlayerConfig("Alice", -1, PlayerType.PERSON),
            PlayerConfig("Bob", -1, PlayerType.PERSON)
        )

        rootService.startGame(playerConfig, GameMode.TWO_PLAYERS)

        val state = checkNotNull(rootService.currentGame)

        state.players[0].currentTile = Tile(TileType.STRAIGHTS_ONLY)
        state.players[1].currentTile = Tile(TileType.STRAIGHTS_ONLY)

        state.drawPile.clear()

        (0..48).forEach {
            state.drawPile.add(Tile(TileType.STRAIGHTS_ONLY))
        }
    }

    /** assert that [Refreshable.onPlayerMove] is called with the correct arguments */
    @Test
    fun refreshableCalledTest() {
        var refreshableCalled = false

        playerService.addRefreshable(object : Refreshable {
            override fun onPlayerMove(
                player: Player,
                nextPlayer: Player,
                tile: Tile,
                position: Pair<Int, Int>,
                rotation: Int
            ) {
                assertEquals(player.name, "Alice")
                assertEquals(nextPlayer.name, "Bob")
                assertEquals(tile.tileType, TileType.STRAIGHTS_ONLY)
                assertEquals(position, Pair(1, -1))
                assertEquals(rotation, 4)

                refreshableCalled = true
            }
        })

        playerService.playerMove(Pair(currentTile(), 4), Pair(1, -1))
        assertTrue(refreshableCalled, "the onPlayerMove refresh was not called")
    }

    /** assert that the current player swaps after each turn */
    @Test
    fun playersTakeTurnsTest() {
        assertEquals(currentPlayer().name, "Alice")
        playerService.playerMove(Pair(currentTile(), 0), Pair(1, -1))
        assertEquals(currentPlayer().name, "Bob")
        playerService.playerMove(Pair(currentTile(), 0), Pair(-1, 1))
        assertEquals(currentPlayer().name, "Alice")
    }

    /** assert that placing a tile next to the center treasure will move an emerald from it */
    @Test
    fun simpleGemMoveTest() {
        playerService.playerMove(Pair(currentTile(), 0), Pair(1, 0))
        assertContains(getTileAt(Pair(1, 0)).gems, Gem.EMERALD)
    }

    /** assert that a saphire is moved from the center tile after all other emerald are removed from it */
    @Test
    fun saphireMoveTest() {
        val positions = listOf(
            Pair(0, 1), Pair(0, -1), Pair(1, 0),
            Pair(-1, 0), Pair(1, -1), Pair(-1, 1)
        )

        positions.forEach {
            playerService.playerMove(Pair(currentTile(), 0), it)
        }

        positions.subList(0, 5).forEach {
            assertContains(getTileAt(it).gems, Gem.EMERALD)
        }

        val lastPos = positions.last()
        assertContains(getTileAt(lastPos).gems, Gem.SAPHIRE)
    }

    /** assert that gems move over multiple tiles after a connecting piece is placed */
    @Test
    fun moveGemOverMultipleTilesTest() {
        playerService.playerMove(Pair(currentTile(), 0), Pair(-2, 2))
        playerService.playerMove(Pair(currentTile(), 0), Pair(-1, 1))

        assertContains(getTileAt(Pair(-2, 2)).gems, Gem.EMERALD)
    }

    /** assert that placing a tile next to an outer treasure tile will move the Amber on it */
    @Test
    fun testOuterTreasureTiles() {
        playerService.playerMove(Pair(currentTile(), 0), Pair(-3, 3))
        assertContains(getTileAt(Pair(-3, 3)).gems, Gem.AMBER)
    }

    /**
     * assert that placing a straight path between the outer and inner treasure will cause
     * two gems to be eliminated from the board
     */
    @Test
    fun testGemEliminationTest() {
        val positions = listOf(
            Pair(-3, 3), Pair(-1, 1), Pair(-2, 2)
        )

        positions.forEach {
            playerService.playerMove(Pair(currentTile(), 0), it)
        }

        positions.forEach {
            getTileAt(it).gems.all { gem -> gem == null }
        }

        getTileAt(Pair(-4, 4)).gems.all { it == null }

        assertEquals(getTileAt(Pair(0, 0)).gems.count { it != null }, 5)
        assertContains(getTileAt(Pair(0, 0)).gems, Gem.SAPHIRE)
    }

    /** utility to get the current game state */
    private fun gameState(): GameState = checkNotNull(rootService.currentGame) {
        "rootService is not initialized"
    }

    /** get the current player */
    private fun currentPlayer(): Player = checkNotNull(gameState().currentPlayer)

    /** get the tile the current player is holding */
    private fun currentTile(): Tile = checkNotNull(gameState().currentPlayer.currentTile) {
        "a player should always be holding a tile after initialization"
    }

    /** get the tile placed at a given board position */
    private fun getTileAt(pos: Pair<Int, Int>): Tile = checkNotNull(gameState().board.grid.grid[pos]) {
        "no tile was placed at (" + pos.first + " " + pos.second + ")"
    }
}