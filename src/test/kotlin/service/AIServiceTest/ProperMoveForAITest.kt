package service.AIServiceTest

import entity.*
import service.*
import kotlin.test.*

class ProperMoveForAITest {

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
        player3 = PlayerConfig("Cortana", 8, PlayerType.PERSON)
        player4 = PlayerConfig("GoogleAssistant", 8, PlayerType.COMPUTER)
    }

    @Test
    fun `test properMoveForAI with ideal conditions`() {
        players = listOf(player1, player2, player3)
        rootService.startGame(players, GameMode.THREE_PLAYERS)

        aiService = AIService(rootService)
        gameState = rootService.currentGame!!
        //gameState.currentPlayer.currentTile = Tile(TileType.STRAIGHT_NOCROSS)
        //var betterMove = aiService.properMoveForAI()
        //rootService.playerService.playerMove(betterMove.first, betterMove.second)

        //rootService.playerService.playerMove(Pair(Tile(TileType.CORNERS_ONLY), 0), Pair(2,-4))
        //gameState.currentPlayer.currentTile = Tile(TileType.CORNERS_ONLY)
        //betterMove = aiService.properMoveForAI()
        //rootService.playerService.playerMove(betterMove.first, betterMove.second)
        //gameState.currentPlayer.currentTile = Tile(TileType.CURVES_TO_CORNER)


        for(i in 0..20) {

            print("Round: ")
            println(i)

            print("Current Player: ")
            println(gameState.currentPlayer.name)
            //RandomAI
            var randomMove = aiService.randomMove()
            print("Tile Type: ")
            print(randomMove.first.first.tileType)
            print(" , Rotation and Coordinates: ")
            print(randomMove.first.second)
            print(", ")
            println(randomMove.second)
            //rootService.playerService.playerMove(randomMove.first, randomMove.second)
            print("Current Player: ")
            println(gameState.currentPlayer.toString())

            //Better AI
            var betterMove = aiService.properMoveForAI()
            print("Tile Type: ")
            print(betterMove.first.first.tileType)
            print(" , Rotation and Coordinates: ")
            print(betterMove.first.second)
            print(", ")
            println(betterMove.second)
            //rootService.playerService.playerMove(betterMove.first, betterMove.second) //Path leads to a tile with no connecting path
        }
        var point1 = 0
        rootService.currentGame!!.players[0].collectedGems.forEach{ gem->

            point1 += gem.score()}
        var point2 = 0
        rootService.currentGame!!.players[1].collectedGems.forEach{ gem->
            point2 += gem.score()}

        println("p2: ")
        println(point2)
        println(", p1: ")
        println(point1)
        //Assertion and fine-tuning can be done when bugs in PlayerActionService are fixed.
        //assertTrue { point2 >= point1 }
    }

    /*
    @Test
    fun `test properMoveForAI with no possible moves`() {
        // Setup a game state where no moves are possible
        // Assert that the method returns a fallback move
    }

    @Test
    fun `test properMoveForAI with multiple equal scoring moves`() {
        // Setup a game state where multiple moves have the same highest score
        // Assert that the method returns one of these moves (if it's deterministic, test for the specific move; if not, just test that it's one of the high-scoring moves)
    }

    @Test
    fun `test properMoveForAI with a complex game state`() {
        // Setup a complex game state
        // Assert that the method returns a valid move, though the "best" move might be subjective in this case
    }

    @Test
    fun `test properMoveForAI verifies use of heuristic function`() {
        // Setup a game state
        // Mock or spy on the `calculateHeuristicScore` function to ensure it's being called correctly
        // Assert that the method returns the expected move based on the mocked heuristic scores
    }
*/
}