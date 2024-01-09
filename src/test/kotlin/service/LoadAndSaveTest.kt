    package service

    import entity.*
    import org.junit.jupiter.api.Assertions.*
    import org.junit.jupiter.api.Test
    import java.io.File

    class LoadAndSaveTest {
        /** for save test , we Create an instance of RootService
         *  Start a new game
         * Save the game to a  file
         * Verify that the file exists
         * load this file
         * Than Compare the loadedGame with the currentGame in rootService
         */

        @Test
        fun saveAndLoadTest() {
            val rootService = RootService()
            val players = listOf(
                PlayerConfig("Player1", 25, PlayerType.PERSON),
                PlayerConfig("Player2", 30, PlayerType.PERSON)
            )
            rootService.startGame(players, GameMode.TWO_PLAYERS)
            val tempFilePath = "Indigo.json"
            rootService.save(tempFilePath)
            val savedFile = File(tempFilePath)
            assertTrue(savedFile.exists())

            val loadedGame: GameState? = rootService.load(tempFilePath)
            assertEquals(loadedGame, rootService.currentGame)
        }

        /**loadEmptyFileTest will :
         *   Save an empty file first
         *   load this file
         *   Verify that the loaded game state is null
         */
        @Test
        fun loadEmptyFileTest() {
            val rootService = RootService()
            val tempFilePath = "Indigo.json"
            File(tempFilePath).writeText("")
            val loadedGame: GameState? = rootService.load(tempFilePath)
            assertNull(loadedGame)
        }
    }
