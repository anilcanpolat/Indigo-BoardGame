package service.aIServiceTest

import entity.*
import service.*
import kotlin.test.*

/**
 * its seems to be a class to test the proper Ai with almost 0 testing being done
 */
class ProperMoveForAITest {

    private lateinit var rootService: RootService
    private lateinit var aiService: AIService
    private lateinit var gameState: GameState

    private lateinit var player1: PlayerConfig
    private lateinit var player2: PlayerConfig
    private lateinit var player3: PlayerConfig
    private lateinit var player4: PlayerConfig
    private lateinit var players: List<PlayerConfig>

    /**
     * set up function to set values before the test runs.
     */
    @BeforeTest
    fun setUp() {

        rootService = RootService()
        player1 = PlayerConfig("Alexa", 10, PlayerType.COMPUTER)
        player2 = PlayerConfig("Siri", 13, PlayerType.COMPUTER)
        player3 = PlayerConfig("Cortana", 8, PlayerType.PERSON)
        player4 = PlayerConfig("GoogleAssistant", 8, PlayerType.COMPUTER)
    }

    /**
     * function to actually test something? not sure, I think it does nothing.
     */
    @Test
    fun `test properMoveForAI with ideal conditions`() {
        players = listOf(player1, player2)
        rootService.startGame(players, GameMode.TWO_PLAYERS)

        aiService = AIService(rootService)
        gameState = rootService.currentGame!!

        /**
        //Manual Moves zur Verfügung

        //gameState.currentPlayer.currentTile = Tile(TileType.STRAIGHT_NOCROSS)
        //var betterMove = aiService.properMoveForAI()
        //rootService.playerService.playerMove(betterMove.first, betterMove.second)

        //rootService.playerService.playerMove(Pair(Tile(TileType.CORNERS_ONLY), 0), Pair(2,-4))
        //gameState.currentPlayer.currentTile = Tile(TileType.CORNERS_ONLY)
        //betterMove = aiService.properMoveForAI()
        //rootService.playerService.playerMove(betterMove.first, betterMove.second)
        //gameState.currentPlayer.currentTile = Tile(TileType.CURVES_TO_CORNER)


        for(i in 1..26) {
            print("Round: ")
            println(i)

            //RandomAI
            print("Current Player(RandomAI/Red): ")
            println(gameState.currentPlayer.name)
            print("Collected before the move: ")
            println(gameState.currentPlayer.collectedGems)
            val randomMove = aiService.randomMove()
            print("Tile Type: ")
            print(randomMove.first.first.tileType)
            print(" , Rotation and Coordinates: ")
            print(randomMove.first.second)
            print(", ")
            println(randomMove.second)
            //rootService.playerService.playerMove(randomMove.first, randomMove.second)

            //Better AI
            print("Current Player(BetterAI/Cyan): ")
            println(gameState.currentPlayer.name)
            print("Collected before the move: ")
            println(gameState.currentPlayer.collectedGems)
            val betterMove = aiService.properMoveForAI()
            print("Tile Type: ")
            print(betterMove.first.first.tileType)
            print(" , Rotation and Coordinates: ")
            print(betterMove.first.second)
            print(", ")
            println(betterMove.second)
            //rootService.playerService.playerMove(betterMove.first, betterMove.second)
        //Path leads to a tile with no connecting path

            /*//Player
            print("Current Player: ")
            println(gameState.currentPlayer.name)*/
        }
        var point1 = 0
        rootService.currentGame!!.players[0].collectedGems.forEach{ gem->

            point1 += gem.score()}
        var point2 = 0
        rootService.currentGame!!.players[1].collectedGems.forEach{ gem->
            point2 += gem.score()}

        print("Player1: ")
        print(point1)
        print(", Player2: ")
        println(point2)
        //Assertion and fine-tuning can be done when bugs in PlayerActionService are fixed.
        //assertTrue { point1 >= point2 }

         */
    }

}