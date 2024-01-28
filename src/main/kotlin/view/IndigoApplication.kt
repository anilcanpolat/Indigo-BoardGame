package view

import entity.*
import kotlin.random.Random
import service.Refreshable
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.core.MenuScene
import view.ui.*
import java.io.File


/**
 * Create scenes and give some buttons there functionality.
 * initialize the scenes afterward
 */
class IndigoApplication : BoardGameApplication("Indigo-Game"), Refreshable {

    private val rootService = RootService()

    private fun generateRandomId(): String {
        val random = Random(System.currentTimeMillis())
        return random.nextInt(1000, 10000).toString()
    }

    private var id = ""

    private var hotSeat = true

    private var gameMode = 0


    private val gameScene : GameScene = GameScene(rootService, this).apply {
        quitButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)
        }

        saveButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(saveAndLoadScene)
        }
    }

    private val welcomeScene : MenuScene = WelcomeScene().apply {
        opacity = 0.9

        loadGameButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(saveAndLoadScene)
                    }

        guestButton.onMouseClicked = {
            hotSeat = false
            this@IndigoApplication.showMenuScene(guestButtonScene)
        }

        hostButton.onMouseClicked = {
            hotSeat = false
            this@IndigoApplication.showMenuScene(hostGameScene)
        }

        hotSeatModeButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(chosePlayerCountScene)
        }

        quitButton.onMouseClicked = {
            exit()
        }
    }


    private val chosePlayerCountScene : ChosePlayerCountScene = ChosePlayerCountScene().apply {
        opacity = 0.9

        backButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)
            hotSeat = true
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

    private val hostGameScene : HostGameScene = HostGameScene().apply {
        backFromHostGameScene.onMouseClicked = {
            hotSeat = true
            this@IndigoApplication.showMenuScene(welcomeScene)
        }

        host2Pl.onMouseClicked = {
            id = generateRandomId()
            rootService.networkService.createGame(id,
                remoteConfigList(1)[0], GameMode.TWO_PLAYERS)
            lobbyScene.lobbyId.text = id
            lobbyScene.disableLabels(2)
            lobbyScene.hostNameLabel.text = hostNameTextfield.text
            this@IndigoApplication.showMenuScene(lobbyScene)
        }

        host3PlShared.onMouseClicked = {
            id = generateRandomId()
            rootService.networkService.createGame(id,
                remoteConfigList(3)[0], GameMode.THREE_PLAYERS_SHARED_GATES)
            lobbyScene.lobbyId.text = id
            lobbyScene.disableLabels(3)
            lobbyScene.hostNameLabel.text = hostNameTextfield.text
            this@IndigoApplication.showMenuScene(lobbyScene)
        }

        host3Pl.onMouseClicked = {
            id = generateRandomId()
            rootService.networkService.createGame(id,
                remoteConfigList(2)[0], GameMode.THREE_PLAYERS)
            lobbyScene.lobbyId.text = id
            lobbyScene.disableLabels(3)
            lobbyScene.hostNameLabel.text = hostNameTextfield.text
            this@IndigoApplication.showMenuScene(lobbyScene)
        }

        host4Pl.onMouseClicked = {
            id = generateRandomId()
            rootService.networkService.createGame(id,
                remoteConfigList(4)[0], GameMode.FOUR_PLAYERS)
            lobbyScene.lobbyId.text = id
            lobbyScene.disableLabels(4)
            lobbyScene.hostNameLabel.text = hostNameTextfield.text
            this@IndigoApplication.showMenuScene(lobbyScene)
        }
    }

    //Select the name and Ki level for a game in the Hotseat mode
    private val selectNameAndKiScene : SelectNameAndKiScene = SelectNameAndKiScene().apply {
        opacity = 0.9

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
                when (gameMode) {
                    1 -> {
                        rootService.startGame(playerConfigList(gameMode, kiA, kiB, kiC, kiD),
                            GameMode.TWO_PLAYERS)
                    }
                    2 -> {
                        rootService.startGame(playerConfigList(gameMode, kiA, kiB, kiC, kiD),
                            GameMode.THREE_PLAYERS)
                    }
                    3 -> {
                        rootService.startGame(playerConfigList(gameMode, kiA, kiB, kiC, kiD),
                            GameMode.THREE_PLAYERS_SHARED_GATES)
                    }
                    4 -> {
                        rootService.startGame(playerConfigList(gameMode, kiA, kiB, kiC, kiD),
                            GameMode.FOUR_PLAYERS)
                    }
                }
            }
        }
    }

    private fun deleteState(file: File){
        file.delete()
    }

    //safe and load functionality
    private val saveAndLoadScene : SaveAndLoadScene = SaveAndLoadScene().apply {
        opacity = 1.0

        returnFromSaveButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)
        }

        saveButtonOne.onMouseClicked = {
            if (!File("savestate1.json").exists()) {
                rootService.save("savestate1.json")
                saveButtonOne.text = "Savestate 1"
            }else{
                rootService.load("savestate1.json")
                this@IndigoApplication.showGameScene(gameScene)
                this@IndigoApplication.hideMenuScene()
            }
        }
        saveButtonTwo.onMouseClicked = {
            if (!File("savestate2.json").exists()) {
                rootService.save("savestate2.json")
                saveButtonTwo.text = "Savestate 2"
            }else{
                rootService.load("savestate2.json")
                this@IndigoApplication.showGameScene(gameScene)
                this@IndigoApplication.hideMenuScene()
            }
        }
        saveButtonThree.onMouseClicked = {
            if (!File("savestate3.json").exists()) {
                rootService.save("savestate3.json")
                saveButtonThree.text = "Savestate 3"
            }else{
                rootService.load("savestate3.json")
                this@IndigoApplication.showGameScene(gameScene)
                this@IndigoApplication.hideMenuScene()
            }
        }
        saveButtonFour.onMouseClicked = {
            if (!File("savestate4.json").exists()) {
                rootService.save("savestate4.json")
                saveButtonFour.text = "Savestate 4"
            }else{
                rootService.load("savestate4.json")
                this@IndigoApplication.showGameScene(gameScene)
                this@IndigoApplication.hideMenuScene()
            }
        }
        saveButtonFive.onMouseClicked = {
            if (!File("savestate5.json").exists()) {
                rootService.save("savestate5.json")
                saveButtonFive.text = "Savestate 5"
            }else{
                rootService.load("savestate5.json")
                this@IndigoApplication.showGameScene(gameScene)
                this@IndigoApplication.hideMenuScene()
            }
        }

        deleteButtonOne.onMouseClicked = {
            if (File("savestate1.json").exists()){
                deleteState(File("savestate1.json"))
                saveButtonOne.text = "Empty: "
            }
        }

        deleteButtonTwo.onMouseClicked = {
            if (File("savestate2.json").exists()){
                deleteState(File("savestate2.json"))
                saveButtonTwo.text = "Empty: "
            }
        }

        deleteButtonThree.onMouseClicked = {
            if (File("savestate3.json").exists()){
                deleteState(File("savestate3.json"))
                saveButtonThree.text = "Empty: "
            }
        }

        deleteButtonFour.onMouseClicked = {
            if (File("savestate4.json").exists()){
                deleteState(File("savestate4.json"))
                saveButtonFour.text = "Empty: "
            }
        }

        deleteButtonFive.onMouseClicked = {
            if (File("savestate5.json").exists()){
                deleteState(File("savestate5.json"))
                saveButtonFive.text = "Empty: "
            }
        }
    }

    private val lobbyScene : LobbyScene = LobbyScene().apply {
        opacity = 0.9

        backFromLobbyScene.onMouseClicked = {
            hotSeat = true
            this@IndigoApplication.showMenuScene(welcomeScene)
        }
    }

    private val endGameScene : EndgameScene = EndgameScene().apply {
        opacity = 1.0
        quitButton.onMouseClicked = {
            exit()
        }

        startGameButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)
        }

    }

    private val guestButtonScene : GuestButtonScene =
        GuestButtonScene(rootService.networkService, this).apply {
            opacity = 0.9

        returnGuestButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)

            joinButton.onMouseClicked = {
                this@IndigoApplication.hideMenuScene()
                this@IndigoApplication.showGameScene(gameScene)
            }
        }
    }

    private fun calcScore(gems : MutableList<Gem>) : Int{
        var score = 0
        for (i in gems){
            score += i.score()
        }
        return score
    }

    /**
     * a help function used for refreshes to hide the scene currently shown
     * and show the gameScene. Used for example in onGameStart.
     */
    fun hideMenuAndShowGame(){
        this@IndigoApplication.showGameScene(gameScene)
        this@IndigoApplication.hideMenuScene()
    }

    /**
     * help function to show the lobby scene when pressing join in the guestButtonScene.
     */
    fun showLobbyScene(){
        this@IndigoApplication.showMenuScene(lobbyScene)
    }

    var list: List<Player> = listOf()
    override fun onGameFinished(players: List<Player>) {
        list = players.sortedByDescending { calcScore(it.collectedGems) }
        when(list.size){
            2 -> {
                endGameScene.winnerOne.text = list[0].name + ": " + calcScore(list[0].collectedGems).toString()
                endGameScene.winnerTwo.text = list[1].name + ": " + calcScore(list[1].collectedGems).toString()
                endGameScene.winnerThree.isVisible = false
                endGameScene.bronze.isVisible = false
                endGameScene.winnerFour.isVisible = false
                endGameScene.fourthPlace.isVisible = false
            }
            3 -> {
                endGameScene.winnerOne.text = list[0].name + ": " + calcScore(list[0].collectedGems).toString()
                endGameScene.winnerTwo.text = list[1].name + ": " + calcScore(list[1].collectedGems).toString()
                endGameScene.winnerThree.text = list[2].name + ": " + calcScore(list[2].collectedGems).toString()
                endGameScene.winnerFour.isVisible = false
                endGameScene.fourthPlace.isVisible = false
            }
            4 -> {
                endGameScene.winnerOne.text = list[0].name + ": " + calcScore(list[0].collectedGems).toString()
                endGameScene.winnerTwo.text = list[1].name + ": " + calcScore(list[1].collectedGems).toString()
                endGameScene.winnerThree.text = list[2].name + ": " + calcScore(list[2].collectedGems).toString()
                endGameScene.winnerFour.text = list[3].name + ": " +  calcScore(list[3].collectedGems).toString()
            }
        }
        this@IndigoApplication.showMenuScene(endGameScene)
    }


    init {
        rootService.addRefreshable(gameScene)
        rootService.addRefreshable(this)
        rootService.addRefreshable(endGameScene)
        rootService.playerService.addRefreshable(gameScene)
        rootService.playerService.addRefreshable(this)
        rootService.playerService.addRefreshable(endGameScene)
        rootService.addRefreshable(saveAndLoadScene)
        rootService.addRefreshable(lobbyScene)
        rootService.playerService.addRefreshable(lobbyScene)
        this.showMenuScene(welcomeScene)
    }

}