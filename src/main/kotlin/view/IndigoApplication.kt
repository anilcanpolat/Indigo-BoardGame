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

    private var isUse1 = false
    private var isUse2 = false
    private var isUse3 = false
    private var isUse4 = false
    private var isUse5 = false

    private val gameScene : GameScene = GameScene(rootService, this).apply {
        quitButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)
        }

        saveButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(saveAndLoadScene)
        }
    }

    private val welcomeScene : MenuScene = WelcomeScene().apply {
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
            lobbyScene.disableLabels(3)
            lobbyScene.hostNameLabel.text = hostNameTextfield.text
            this@IndigoApplication.showMenuScene(lobbyScene)
        }
    }

    //Select the name and Ki level for a game in the Hotseat mode
    private val selectNameAndKiScene : SelectNameAndKiScene = SelectNameAndKiScene().apply {
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
        returnFromSaveButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)
        }

        saveButtonOne.onMouseClicked = {
            if (!isUse1) {
                rootService.save("savestate1.json")
                saveButtonOne.text = "Savestate 1"
                isUse1 = true
            }else{
                rootService.load("savestate1.json")
            }
        }
        saveButtonTwo.onMouseClicked = {
            if (!isUse2) {
                rootService.save("savestate2.json")
                saveButtonTwo.text = "Savestate 2"
                isUse2 = true
            }else{
                rootService.load("savestate2.json")
            }
        }
        saveButtonThree.onMouseClicked = {
            if (!isUse3) {
                rootService.save("savestate3.json")
                saveButtonTwo.text = "Savestate 3"
                isUse3 = true
            }else{
                rootService.load("savestate3.json")
            }
        }
        saveButtonFour.onMouseClicked = {
            if (!isUse4) {
                rootService.save("savestate4.json")
                saveButtonTwo.text = "Savestate 4"
                isUse4 = true
            }else{
                rootService.load("savestate4.json")
            }
        }
        saveButtonFive.onMouseClicked = {
            if (!isUse5) {
                rootService.save("savestate5.json")
                saveButtonTwo.text = "Savestate 5"
                isUse5 = true
            }else{
                rootService.load("savestate5.json")
            }
        }

        deleteButtonOne.onMouseClicked = {
            if (isUse1){
                deleteState(File("savestate1.json"))
                saveButtonOne.text = "Empty: "
                isUse1 = false
            }
        }

        deleteButtonTwo.onMouseClicked = {
            if (isUse2){
                deleteState(File("savestate2.json"))
                saveButtonTwo.text = "Empty: "
                isUse2 = false
            }
        }

        deleteButtonThree.onMouseClicked = {
            if (isUse3){
                deleteState(File("savestate3.json"))
                saveButtonThree.text = "Empty: "
                isUse3 = false
            }
        }

        deleteButtonFour.onMouseClicked = {
            if (isUse4){
                deleteState(File("savestate4.json"))
                saveButtonFour.text = "Empty: "
                isUse4 = false
            }
        }

        deleteButtonFive.onMouseClicked = {
            if (isUse5){
                deleteState(File("savestate5.json"))
                saveButtonFive.text = "Empty: "
                isUse5 = false
            }
        }
    }

    private val lobbyScene : LobbyScene = LobbyScene().apply {
        backFromLobbyScene.onMouseClicked = {
            hotSeat = true
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

    private val guestButtonScene : GuestButtonScene =
        GuestButtonScene(rootService.networkService, this).apply {

        returnGuestButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)

            joinButton.onMouseClicked = {
                this@IndigoApplication.hideMenuScene()
                this@IndigoApplication.showGameScene(gameScene)
            }
        }
    }

    var list: List<Player> = listOf()

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

    override fun onGameFinished(players: List<Player>) {
        list = players
        list.sortedByDescending { calcScore(it.collectedGems)  }
        when(list.size){
            2 -> {
                endGameScene.winnerOne.text = list[0].name + ": " + calcScore(list[0].collectedGems).toString()
                endGameScene.winnerTwo.text = list[1].name + ": " + calcScore(list[1].collectedGems).toString()
            }
            3 -> {
                endGameScene.winnerOne.text = list[0].name + ": " + calcScore(list[0].collectedGems).toString()
                endGameScene.winnerTwo.text = list[1].name + ": " + calcScore(list[1].collectedGems).toString()
                endGameScene.winnerThree.text = list[2].name + ": " + calcScore(list[2].collectedGems).toString()
            }
            4 -> {
                endGameScene.winnerOne.text = list[0].name + ": " + calcScore(list[0].collectedGems).toString()
                endGameScene.winnerTwo.text = list[1].name + ": " + calcScore(list[1].collectedGems).toString()
                endGameScene.winnerThree.text = list[2].name + ": " + calcScore(list[2].collectedGems).toString()
                endGameScene.winnerFour.text = list[3].name + ": " +  calcScore(list[3].collectedGems).toString()
            }
        }
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
        rootService.addRefreshable(lobbyScene)
        rootService.playerService.addRefreshable(lobbyScene)
        this.showMenuScene(welcomeScene)
    }

}