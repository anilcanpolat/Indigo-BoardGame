package service

import entity.*
import kotlin.test.*

/**
 * Test class for AIService.randomMove method.
 */
class RandomMoveTest {

    private lateinit var rootService: RootService
    private lateinit var aiService: AIService
    private lateinit var gameState: GameState

    private lateinit var player1: PlayerConfig
    private lateinit var player2: PlayerConfig
    private lateinit var player3: PlayerConfig
    private lateinit var player4: PlayerConfig
    private lateinit var players: List<PlayerConfig>

    @BeforeTest
    fun setUp() {

        rootService = RootService()
        player1 = PlayerConfig("Alexa", 10, PlayerType.COMPUTER)
        player2 = PlayerConfig("Siri", 13, PlayerType.COMPUTER)
        player3 = PlayerConfig("Cortana", 8, PlayerType.COMPUTER)
        player4 = PlayerConfig("GoogleAssistant", 8, PlayerType.COMPUTER)
    }

    @Test
    fun randomMoveWithTwoPlayers() {

        players = listOf(player1, player2)
        rootService.startGame(players, GameMode.TWO_PLAYERS)

        aiService = AIService(rootService)
        gameState = rootService.currentGame!!

        val randomMove = aiService.randomMove()
        assertNotEquals(randomMove, aiService.randomMove())
    }

    @Test
    fun randomMoveWithThreePlayers() {

        players = listOf(player1, player2, player3)
        rootService.startGame(players, GameMode.THREE_PLAYERS)

        aiService = AIService(rootService)
        gameState = rootService.currentGame!!

        val randomMove = aiService.randomMove()
        assertNotEquals(randomMove, aiService.randomMove())
    }

    @Test
    fun randomMoveWithSharedGates() {

        players = listOf(player1, player2, player3)
        rootService.startGame(players, GameMode.THREE_PLAYERS)

        aiService = AIService(rootService)
        gameState = rootService.currentGame!!

        val randomMove = aiService.randomMove()
        assertNotEquals(randomMove, aiService.randomMove())

    }

    @Test
    fun randomMoveWithFourPlayers() {

        players = listOf(player1, player2, player3, player4)
        rootService.startGame(players, GameMode.FOUR_PLAYERS)

        aiService = AIService(rootService)
        gameState = rootService.currentGame!!

        val randomMove = aiService.randomMove()
        assertNotEquals(randomMove, aiService.randomMove())

    }
}