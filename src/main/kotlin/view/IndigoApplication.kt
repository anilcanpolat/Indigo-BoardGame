package view

import entity.*
import service.NetworkService
import kotlin.random.Random
import service.Refreshable
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.core.MenuScene
import view.ui.*


/**
 * Create scenes and give some buttons there functionality.
 * initialize the scenes afterward
 */
class IndigoApplication : BoardGameApplication("Indigo-Game"), Refreshable {

    private val rootService = RootService()
    private val networkService = NetworkService(rootService)

    private fun generateRandomId(): String {
        val random = Random(System.currentTimeMillis())
        return random.nextInt(1000, 10000).toString()
    }
    private val id = generateRandomId()

    private var hotSeat = true

    private var gameMode = 0

    private val gameScene : GameScene = GameScene(rootService).apply {
        quitButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)
        }
    }

    private val welcomeScene : MenuScene = WelcomeScene(rootService).apply {
        loadGameButton.onMouseClicked = {
            this@IndigoApplication.showGameScene(gameScene)
            hideMenuScene()
        }

        hostButton.onMouseClicked = {
            hotSeat = false
            this@IndigoApplication.showMenuScene(chosePlayerCountScene)
        }

        hotSeatModeButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(chosePlayerCountScene)
        }

        quitButton.onMouseClicked = {
            exit()
        }
    }


    private val chosePlayerCountScene : ChosePlayerCountScene = ChosePlayerCountScene().apply {

        backButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)
            hotSeat = false
       }

       p2Button.onMouseClicked = {
           gameMode = 1
           selectNameAndKiScene.apply {
               setAmountOfPlayers(2)
           }
           this@IndigoApplication.showMenuScene(selectNameAndKiScene)
       }

       p3OwnButton.onMouseClicked = {
           gameMode = 2
           selectNameAndKiScene.apply {
               setAmountOfPlayers(1)
           }
           this@IndigoApplication.showMenuScene(selectNameAndKiScene)
       }

       p3SharedButton.onMouseClicked = {
           gameMode = 3
           selectNameAndKiScene.apply {
               setAmountOfPlayers(1)
           }
           this@IndigoApplication.showMenuScene(selectNameAndKiScene)
       }

       p4Button.onMouseClicked = {
           gameMode = 4
           this@IndigoApplication.showMenuScene(selectNameAndKiScene)
       }
    }

    private val selectNameAndKiScene : SelectNameAndKiScene = SelectNameAndKiScene(rootService).apply {
        returnFromNameButton.onMouseClicked = {
            resetSceneOnReturn()
            kiA = false
            kiB = false
            kiC = false
            kiD = false
            gameMode = 0
            this@IndigoApplication.showMenuScene(chosePlayerCountScene)
        }

        startGameButton.onMouseClicked = {
            if (hotSeat)
            {
                if(gameMode == 1) {
                    rootService.startGame(playerConfigList(gameMode, kiA, kiB, kiC, kiD),
                        GameMode.TWO_PLAYERS)
                }
                else if(gameMode == 2) {
                    rootService.startGame(playerConfigList(gameMode, kiA, kiB, kiC, kiD),
                        GameMode.THREE_PLAYERS)
                }
                else if(gameMode == 3) {
                    rootService.startGame(playerConfigList(gameMode, kiA, kiB, kiC, kiD),
                        GameMode.THREE_PLAYERS_SHARED_GATES)
                }
                else if(gameMode == 4) {
                    rootService.startGame(playerConfigList(gameMode, kiA, kiB, kiC, kiD),
                        GameMode.FOUR_PLAYERS)
                }/*else{
                if(gameMode == 1) {
                    networkService.createGame(id, remoteConfigList(gameMode),
                        GameMode.TWO_PLAYERS)
                }
                else if(gameMode == 2) {
                    networkService.createGame(id, remoteConfigList(gameMode),
                        GameMode.THREE_PLAYERS)
                }
                else if(gameMode == 3) {
                    networkService.createGame(id, remoteConfigList(gameMode),
                        GameMode.THREE_PLAYERS_SHARED_GATES)
                }
                else if(gameMode == 4) {
                    networkService.createGame(id, remoteConfigList(gameMode),
                        GameMode.FOUR_PLAYERS)
                }

            }*/
            }
            this@IndigoApplication.showGameScene(gameScene)
            this@IndigoApplication.hideMenuScene()
        }
    }

    private val saveAndLoadScene : SaveAndLoadScene = SaveAndLoadScene().apply {
        returnFromSaveButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)
        }
    }

    private val endGameScene : EndgameScene = EndgameScene().apply {
        quitButton.onMouseClicked = {
            exit()
        }

        startGameButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)
        }


    }

    var gameFinishedPlayerList : List<Player> = listOf()
    override fun onGameFinished(players: List<Player>) {
        gameFinishedPlayerList = players
        this.showMenuScene(endGameScene)
    }


    init {
        rootService.addRefreshable(gameScene)
        rootService.addRefreshable(this)
        rootService.addRefreshable(endGameScene)
        rootService.playerService.addRefreshable(gameScene)
        rootService.playerService.addRefreshable(this)
        rootService.playerService.addRefreshable(endGameScene)
        rootService.addRefreshable(saveAndLoadScene)
        this.showMenuScene(endGameScene)
    }

}