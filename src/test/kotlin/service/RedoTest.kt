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
        //nur um Testen ,habe ich nexstate=currentGame genommen statt ganz neue implementierung zu machen (um nextstate !=null)
        rootService.currentGame!!.nextState = rootService.currentGame
        val nextstate= rootService.currentGame!!.nextState
        val gamestate=rootService.currentGame
        rootService.redo()
        assertEquals(rootService.currentGame!!.previousState,gamestate)
        assertEquals(rootService.currentGame!!,nextstate)
    }
}