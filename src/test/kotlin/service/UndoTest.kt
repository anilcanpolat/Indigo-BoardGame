package service
import entity.GameMode
import entity.PlayerConfig
import entity.PlayerType
import kotlin.test.*
import org.junit.jupiter.api.assertThrows
/**
 * Class that provides tests for Undo.
 *
 */
class UndoTest {
    /**
     * Tests the undo function of RootService.
     */

    @Test
    fun testUndo() {
        val rootService = RootService()
        val player1 = PlayerConfig( "bob", 20, PlayerType.PERSON)
        val player2 = PlayerConfig( "jack", 23, PlayerType.PERSON)

        assertThrows<IllegalStateException> {
            rootService.undo()
        }
        rootService.startGame(listOf(player1, player2),GameMode.TWO_PLAYERS)
        //nur um Testen ,habe ich previousstate=currentGame genommen statt ganz neue implementierung zu machen (um previous !=null)
        rootService.currentGame!!.previousState = rootService.currentGame
        val previousstate= rootService.currentGame!!.previousState
        val gamestate=rootService.currentGame
        rootService.undo()
        assertEquals(rootService.currentGame!!.nextState,gamestate)
        assertEquals(rootService.currentGame!!,previousstate)

    }
}