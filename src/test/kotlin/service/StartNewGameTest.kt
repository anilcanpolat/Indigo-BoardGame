package service
import kotlin.test.*
import org.junit.jupiter.api.assertThrows
import entity.*


/**
 * Class that provides tests for startGame.
 *
 */
class StartNewGameTest {
    /** Tests the startNewGame function of RootService. */
    @Test
    fun teststartNewGame() {
        val rootService = RootService()
        val player1 =PlayerConfig( "bob", 20, PlayerType.PERSON)
        val player2 =PlayerConfig( "jack", 23, PlayerType.PERSON)

        assertThrows<IllegalArgumentException> {
        rootService.startGame(listOf(player1, player2),GameMode.FOUR_PLAYERS)
        }
        rootService.startGame(listOf(player1, player2),GameMode.TWO_PLAYERS)
        assertEquals(true, rootService.currentGame != null)
        assertEquals(2, rootService.currentGame?.players?.size)
        // Assert that all players have unique tokens
        val distinctTokens = rootService.currentGame?.players?.map { it.playerToken }?.distinct()
        assertEquals(2, distinctTokens?.size)
    }
}