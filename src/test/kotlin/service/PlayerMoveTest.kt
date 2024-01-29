package service

import entity.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random
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
    }

    /**
     * Test cases that require the draw pile to be filled with tiles of type [TileType.STRAIGHTS_ONLY]
     */
    @Nested
    inner class StraightPathTests {
        /** fill the draw pile with tiles of type [TileType.STRAIGHTS_ONLY] */
        @BeforeTest
        fun fixDrawPile() {
            val pile = gameState().drawPile
            pile.clear()

            (0..48).forEach {
                pile.add(Tile(TileType.STRAIGHTS_ONLY))
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
            assertTrue {getTileAt(Pair(0, 0)).gems.contains(Gem.SAPHIRE)  }
            positions.forEach {
                playerService.playerMove(Pair(currentTile(), 0), it)
            }
            var count =0
            positions.subList(0, 6).forEach {
                if(getTileAt(it).gems.contains(Gem.EMERALD)){
                    count +=1
                }
            }
            assertEquals(count,5)
            assertFalse {getTileAt(Pair(0, 0)).gems.contains(Gem.SAPHIRE)  }
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
                Pair(3, -3), Pair(2, -2), Pair(1, -1)

            )
             positions.forEach {
                playerService.playerMove(Pair(currentTile(), 0), it)
            }

            positions.forEach {
                assertTrue(getTileAt(it).gems.all { gem -> gem == null })
            }

            assertTrue(getTileAt(Pair(4, -4)).gems.all { it == null })

            assertEquals(getTileAt(Pair(0, 0)).gems.count { it != null }, 5)
            assertContains(getTileAt(Pair(0, 0)).gems, Gem.SAPHIRE)

        }

        /**
         * test if gems get eliminated if they run into each other via tiles placed together.
         */
        @Test
        fun testGemEliminationDirectCenterContact() {
            val positions = listOf(
                Pair(-3, 3), Pair(-1, 1), Pair(-2, 2)

            )

            positions.forEach {
                playerService.playerMove(Pair(currentTile(), 0), it)
            }

            positions.forEach {
                assertTrue(getTileAt(it).gems.all { gem -> gem == null})
            }

            assertTrue(getTileAt(Pair(-4, 4)).gems.all { it == null })
            assertTrue(getTileAt(Pair(0, 0)).gems.any { it == null })
        }
    }

    /**
     * Connect the uppermost treasure tile to the next gateway using one [TileType.CORNERS_ONLY]
     * and one [TileType.STRAIGHTS_ONLY]. Assert that the gem is moved over the correct edges
     * and awarded to the correct player.
     */
    @Test
    fun gemToGatewayTest() {
        gameState().currentPlayer.currentTile = Tile(TileType.CORNERS_ONLY)
        playerService.playerMove(Pair(currentTile(), 0), Pair(0, -3))

        assertEquals(getTileAt(Pair(0, -3)).gems[5], Gem.AMBER, "amber should have been moved to edge number 5")

        gameState().currentPlayer.currentTile = Tile(TileType.STRAIGHTS_ONLY)
        playerService.playerMove(Pair(currentTile(), 0), Pair(-1, -3))

        assert(getTileAt(Pair(0, -3)).gems.all { it == null }) { "gems should have been moved off the tile" }
        assert(getTileAt(Pair(-1, -3)).gems.all { it == null }) { "gems should have been moved off the tile" }

        val awardedToken = gameState().board.gates[0]

        gameState().players.any {
            it.playerToken == awardedToken.first && it.collectedGems.contains(Gem.AMBER)
        }
    }

    /**
     * Make sure that placing two tiles at the same position fails. The UI should
     * make sure that no double placements are possible, e.g. by disabling buttons
     * after each invocation of [Refreshable.onPlayerMove].
     */
    @Test
    fun doublePlacementFails() {
        playerService.playerMove(Pair(currentTile(), 0), Pair(0, -1))

        assertFails("placing two tiles at the same position should not be possible") {
            playerService.playerMove(Pair(currentTile(), 0), Pair(0, -1))
        }
    }

    /**
     * Make sure that blocking a gate (by using a [TileType.CORNERS_ONLY] on
     * the outer ring for example) fails. There might be better mechanisms to
     * notify the UI layer of an invalid placement.
     */
    @Test
    fun gateBlockingFails() {
        currentPlayer().currentTile = Tile(TileType.CORNERS_ONLY)

        assertFails("blocking gates using a tile should not be possible") {
            playerService.playerMove(Pair(currentTile(), 0), Pair(-1, -3))
        }
    }

    /** make sure that invalid moves will not affect the game state */
    @Test
    fun invalidMovesFailTest() {
        val invalidMoves: MutableList<Pair<Pair<Tile, Int>, Pair<Int, Int>>> = mutableListOf()

        while (invalidMoves.size < 10000) {
            val pos = Pair(Random.nextInt(-4, 5), Random.nextInt(-4, 5))
            val rot = Random.nextInt(0, 6)
            val tile = randomTile()

            val gameStateCopy = gameState().deepCopy()
            gameStateCopy.currentPlayer.currentTile = tile

            if (!CommonMethods.isValidMove(gameStateCopy, tile, rot, pos)) {
                invalidMoves.add(Pair(Pair(tile, rot), pos))
            }
        }

        invalidMoves.forEach {
            gameState().currentPlayer.currentTile = it.first.first
            val copy = gameState().deepCopy()

            assertThrows<IllegalStateException> {
                playerService.playerMove(it.first, it.second)
            }

            assertEquals(copy, gameState())
        }
    }

    /**
     * make sure that connecting outer treasure tiles with the center
     * tile behaves correctly by eliminating the amber and emerald
     * for all possible configurations
     */
    @Test
    fun testGemElimination() {
        val directions = listOf(
            Pair(1, 0), Pair(-1, 0), Pair(0, 1),
            Pair(0, -1), Pair(1, -1), Pair(-1, 1)
        )


        val positions = directions.map { direction ->
            (1..3).map {
                Pair(it * direction.first, it * direction.second)
            }
        }

        println(positions)

        val corners = directions.map {
            Pair(4 * it.first, 4 * it.second)
        }

        positions.zip(corners).forEach {
            val pos = it.first
            val corner = it.second

            val permutations = listOf(
                listOf(0, 1, 2), listOf(0, 2, 1),
                listOf(1, 0, 2), listOf(1, 2, 0),
                listOf(2, 0, 1), listOf(2, 1, 0)
            )

            permutations.forEach { perm ->
                val order = listOf(
                    pos[perm[0]], pos[perm[1]], pos[perm[2]]
                )

                rootService = createStraightTilesGame()
                playerService = rootService.playerService

                order.forEach { tilePosition ->
                    playerService.playerMove(Pair(currentTile(), 0), tilePosition)
                }

                val center = getTileAt(Pair(0, 0))
                val border = getTileAt(corner)

                assertTrue(border.gems.all { gem -> gem == null },
                    "amber should have been moves of the border treasure tile")
                assertTrue(center.gems.any { gem -> gem == Gem.SAPHIRE }, "the saphire should remain on the treasure tile")

                val centerGemCount = center.gems.filterNotNull().size
                assertEquals(centerGemCount, 5, "only one gem should be removed from the center")
            }
        }
    }

    /** make sure that finishing the game works reliably by playing multiple games with random valid moves */
    @Test
    fun finishWithRandomMovesTest() {
        repeat(1001) {
            val serv = RootService()

            val players = listOf(
                PlayerConfig("Alice", -1, PlayerType.PERSON),
                PlayerConfig("Bob", -1, PlayerType.PERSON)
            )

            serv.startGame(players, GameMode.TWO_PLAYERS)

            var gameFinished = false
            var iterCount = 0

            serv.playerService.addRefreshable(object : Refreshable {
                override fun onGameFinished(players: List<Player>) {
                    gameFinished = true
                }
            })

            while (!gameFinished && iterCount < 100) {
                val move = CommonMethods.calculateRandomAIMove(checkNotNull(serv.currentGame))
                serv.playerService.playerMove(move.first, move.second)

                iterCount += 1
            }

            assertTrue(iterCount < 100, "more iterations executed than possible")
        }
    }

    private fun createStraightTilesGame(): RootService {
        val serv = RootService()

        val playerConfig = listOf(
            PlayerConfig("Alice", -1, PlayerType.PERSON),
            PlayerConfig("Bob", -1, PlayerType.PERSON)
        )

        serv.startGame(playerConfig, GameMode.TWO_PLAYERS)

        val state = checkNotNull(rootService.currentGame)

        state.players[0].currentTile = Tile(TileType.STRAIGHTS_ONLY)
        state.players[1].currentTile = Tile(TileType.STRAIGHTS_ONLY)

        val pile = state.drawPile
        pile.clear()

        (0..48).forEach {
            pile.add(Tile(TileType.STRAIGHTS_ONLY))
        }

        return serv
    }

    /** generate a random tile object */
    private fun randomTile(): Tile {
        val rotation = Random.nextInt(0, 6)
        val tileType = TileType.values().filter {
            it != TileType.TREASURE_CENTER && it != TileType.TREASURE_CORNER
        }.random()

        val tile = Tile(tileType)
        tile.rotate(rotation)

        return tile
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