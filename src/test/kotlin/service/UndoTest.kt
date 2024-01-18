package service
import entity.*
import kotlin.test.*
import org.junit.jupiter.api.assertThrows

/**
 * Class that provides tests for Undo.
 *
 */
/**
 * Class that provides tests for Undo.
 *
 */
class UndoTest {
    /** Tests the undo function of RootService. */
    @Test
    fun testUndo() {
        val rootService = RootService()
        val player1 = PlayerConfig( "bob", 20, PlayerType.PERSON)
        val player2 = PlayerConfig( "jack", 23, PlayerType.PERSON)

        assertThrows<IllegalStateException> {
            rootService.undo()
        }

        rootService.startGame(listOf(player1, player2),GameMode.TWO_PLAYERS)

        val gameState = checkNotNull(rootService.currentGame)
        val prevState = gameState.deepCopy()

        gameState.previousState = prevState
        prevState.nextState = gameState

        rootService.undo()

        val currentState = checkNotNull(rootService.currentGame)

        assertEquals(currentState, prevState)
        assertEquals(currentState.nextState, gameState)
    }
}
