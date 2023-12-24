package service

import entity.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.File

class LoadAndSaveTest {
    /** for save test , we Create an instance of RootService
     *  Start a new game
     * Save the game to a temporary file
     * Verify that the file exists
     * Than we delete the temporary file
     */

    @Test
    fun savetest() {
        val rootService = RootService()
        val players = listOf(
            PlayerConfig("Player1", 25, PlayerType.PERSON),
            PlayerConfig("Player2", 30, PlayerType.PERSON)
        )
        rootService.startGame(players, GameMode.TWO_PLAYERS)
        val tempFilePath = "path/to/temp/game_temp.json"
        rootService.save(tempFilePath)
        val savedFile = File(tempFilePath)
        assert(savedFile.exists())
        savedFile.delete()
    }

    /**for load test , we Create an instance of RootService
     *Create a temporary file and save a sample game state
     * Load the game from the temporary file
     * Verify that the loaded game is not null and has the expected properties
     * Than we delete the temporary file
     */
    @Test
    fun loadtest() {
        val rootService = RootService()
        val tempFilePath = "path/to/temp/game_temp.json"
        val sampleGameState =
            rootService.currentGame?.let {
                GameState(Player("SamplePlayer", 25, PlayerType.PERSON, PlayerToken.RED,
                    Tile(TileType.CORNERS_ONLY)), it.board, emptyList(), mutableListOf())
            }

        val jsonStr = Json.encodeToString(sampleGameState)
        File(tempFilePath).writeText(jsonStr)
        rootService.load(tempFilePath)
        assertNotNull(rootService.currentGame)
        assertEquals("SamplePlayer", rootService.currentGame?.currentPlayer?.name)
        File(tempFilePath).delete()
    }
}