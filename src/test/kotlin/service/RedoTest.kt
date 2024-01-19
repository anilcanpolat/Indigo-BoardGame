package service
import entity.*
import kotlin.test.*
import org.junit.jupiter.api.assertThrows
/**
 * Class that provides tests for Redo.
 *
 */
class RedoTest {
    /** Tests the redo function of RootService.*/
    @Test
    fun testRedo() {
        val rootService = RootService()
        val player1 = PlayerConfig( "bob", 20, PlayerType.PERSON)
        val player2 = PlayerConfig( "jack", 23, PlayerType.PERSON)

        assertThrows<IllegalStateException> {
            rootService.redo()
        }

        rootService.startGame(listOf(player1, player2),GameMode.TWO_PLAYERS)

        val gameState = checkNotNull(rootService.currentGame)
        val nextState = gameState.deepCopy()

        gameState.nextState = nextState
        nextState.previousState = gameState


        rootService.redo()

        assertEquals(checkNotNull(rootService.currentGame).previousState, gameState)
        assertEquals(checkNotNull(rootService.currentGame), nextState)
    }
}