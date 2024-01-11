package service

import entity.GameMode
import entity.GameState
import entity.PlayerConfig
import entity.PlayerType
import kotlin.test.*
import java.io.File

/**
 * Test cases for [RootService.save] and [RootService.load].
 */
class LoadAndSaveTest {
    /**
     * Make sure that serialization and deserialization of the full [GameState]
     * with set instances of [GameState.previousState] and [GameState.nextState]
     * is done correctly.
     *
     * The [GameState] is mutated to appear as if two moves have already been executed
     * and [RootService.undo] has been called. So [RootService.currentGame] should point
     * to the second most recent [GameState] instance.
     *
     * After that the [RootService.currentGame] is serialized and written to a temporary
     * file. The file will be deleted after completing the test. The temporary file
     * is then reloaded by a different [RootService] instance. Both contained [GameState]
     * objects are compared for equality, including the previous and next states.
     */
    @Test
    fun saveAndLoadTest() {
        val rootService = RootService()

        val players = listOf(
            PlayerConfig("Alice", -1, PlayerType.PERSON),
            PlayerConfig("Bob", -1, PlayerType.PERSON)
        )

        rootService.startGame(players, GameMode.TWO_PLAYERS)

        val currentGame = checkNotNull(rootService.currentGame)

        // Create two fake moves
        val nextState = GameState(
            currentGame.currentPlayer,
            currentGame.board,
            currentGame.players,
            currentGame.drawPile
        )

        val nextNextState = GameState(
            currentGame.currentPlayer,
            currentGame.board,
            currentGame.players,
            currentGame.drawPile
        )

        currentGame.nextState = nextState
        nextState.nextState = nextNextState
        nextState.previousState = currentGame
        nextNextState.previousState = nextState

        rootService.redo() // jump to nextState

        val tmpFile = File.createTempFile("savestate", ".json")
        tmpFile.deleteOnExit()

        rootService.save(tmpFile)

        val secondRoot = RootService()
        secondRoot.load(tmpFile)

        val testGame = checkNotNull(secondRoot.currentGame) { "state not restored" }
        val curGame = checkNotNull(rootService.currentGame)

        assertTrue(gameStateEqual(testGame, curGame), "restored gamestate does not equal the previous one")

        assertTrue(
            gameStateEqual(
                checkNotNull(testGame.previousState) { "previous state was not restored" },
                checkNotNull(curGame.previousState)
            ),
            "previous gamestate does not equal the previous previous gamestate one"
        )

        assertTrue(
            gameStateEqual(
                checkNotNull(testGame.nextState) { "next state was not restored" },
                checkNotNull(curGame.nextState)
            ),
            "next gamestate does not equal the previous next gamestate one"
        )
    }

    /**
     * Check whether two [GameState] instances are equal, without considering
     * the equality of the [GameState.nextState] and [GameState.previousState] fields.
     */
    private fun gameStateEqual(fst: GameState, snd: GameState): Boolean =
        fst.currentPlayer == snd.currentPlayer &&
                fst.players == snd.players &&
                fst.drawPile == snd.drawPile &&
                fst.board == snd.board
}
