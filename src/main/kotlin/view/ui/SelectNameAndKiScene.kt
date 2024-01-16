package view.ui

import entity.PlayerConfig
import entity.PlayerType
import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.CheckBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual

/**
 * scene that allows the player to input there name and other utilities
 * required for the game to start. only shows properties that are required for
 * the amount of players selected. also gives us control over KI.
 */
class SelectNameAndKiScene(rootService: RootService) : MenuScene(1920, 1080,
    background = ImageVisual("cecihoney-background-desert-full.jpg")) {

    val returnFromNameButton = Button(
        posX = 10, posY = 10,
        width = 40, height = 40,
        visual = CompoundVisual(
            ImageVisual(
                path = "blackArrow.png")
        )
    )

    //textfields for player names
    private val playerATextBox = TextField(
        posX = 760, posY = 200,
        width = 250, height = 50,
        prompt = "Put in Name! "
    )

    private val playerBTextBox = TextField(
        posX = 760, posY = 350,
        width = 250, height = 50,
        prompt = "Put in Name! "
    )

    private val playerCTextBox = TextField(
        posX = 760, posY = 500,
        width = 250, height = 50,
        prompt = "Put in Name! "
    )

    val playerDTextBox = TextField(
        posX = 760, posY = 650,
        width = 250, height = 50,
        prompt = "Put in Name! "
    )

    // KI Buttons
    val kiButtonA = Button(
        posX = 1035, posY = 200,
        width = 50, height = 50,
        text = "KI: "
    ).apply { visual = ColorVisual(193, 74, 240) }

    val kiButtonB = Button(
        posX = 1035, posY = 350,
        width = 50, height = 50,
        text = "KI: "
    ).apply { visual = ColorVisual(193, 74, 240) }

    val kiButtonC = Button(
        posX = 1035, posY = 500,
        width = 50, height = 50,
        text = "KI: "
    ).apply { visual = ColorVisual(193, 74, 240) }

    val kiButtonD = Button(
        posX = 1035, posY = 650,
        width = 50, height = 50,
        text = "KI: "
    ).apply { visual = ColorVisual(193, 74, 240) }


    //SequenzButtons
    private val playerSequenzAButton = Button(
        posX = 1110, posY = 200,
        width = 50, height = 50,
        text = "1: "
    ).apply { visual = ColorVisual(90, 74, 240) }

    private val playerSequenzBButton = Button(
        posX = 1110, posY = 350,
        width = 50, height = 50,
        text = "2: "
    ).apply { visual = ColorVisual(90, 74, 240) }

    private val playerSequenzCButton = Button(
        posX = 1110, posY = 500,
        width = 50, height = 50,
        text = "3: "
    ).apply { visual = ColorVisual(90, 74, 240) }

    val playerSequenzDButton = Button(
        posX = 1110, posY = 650,
        width = 50, height = 50,
        text = "4: "
    ).apply { visual = ColorVisual(90, 74, 240) }

    //Ki Speed stuff
    private val labelKiSpeedPartA = Label(
        posX = 770, posY = 785,
        width = 75, height = 50,
        text = "Ki-Speed: "
    )

    private val labelKiSpeedPartB = Label(
        posX = 895, posY = 785,
        width = 50, height = 50,
        text = "ms "
    )

    private val kiSpeedTextField = TextField(
        posX = 855, posY = 795,
        width = 40, height = 30,
        text = "250"
    )

    //Random player sequence
    private val playerSequenceLabel = Label(
        posX = 770, posY = 840,
        width = 140, height = 50,
        text = "Random Sequence: "
    )

    private val randomCheckbox = CheckBox(
        posX = 910, posY = 840,
        width = 150, height = 50
    )


    private val startGameButton = Button(
        posX = 955, posY = 790,
        width = 200, height = 100,
        text = "Start!", font = Font(50)
    ).apply { visual = ColorVisual(ColorEnum.Olivine.toRgbValue()) }

    fun setAmountOfPlayers(playerToRemove: Int){
        if (playerToRemove == 2) {
            kiButtonC.isDisabled = true
            kiButtonC.isVisible = false
            playerCTextBox.isDisabled = true
            playerCTextBox.isVisible = false
            playerSequenzCButton.isDisabled = true
            playerSequenzCButton.isVisible = false

            kiButtonD.isDisabled = true
            kiButtonD.isVisible = false
            playerDTextBox.isDisabled = true
            playerDTextBox.isVisible = false
            playerSequenzDButton.isDisabled = true
            playerSequenzDButton.isVisible = false
        }
        else if (playerToRemove == 1)
        {
            kiButtonD.isDisabled = true
            kiButtonD.isVisible = false
            playerDTextBox.isDisabled = true
            playerDTextBox.isVisible = false
            playerSequenzDButton.isDisabled = true
            playerSequenzDButton.isVisible = false
        }
    }

    fun resetSceneOnReturn(){
        kiButtonC.isDisabled = false
        kiButtonC.isVisible = true
        playerCTextBox.isDisabled = false
        playerCTextBox.isVisible = true
        playerSequenzCButton.isDisabled = false
        playerSequenzCButton.isVisible = true

        kiButtonD.isDisabled = false
        kiButtonD.isVisible = true
        playerDTextBox.isDisabled = false
        playerDTextBox.isVisible = true
        playerSequenzDButton.isDisabled = false
        playerSequenzDButton.isVisible = true

        playerATextBox.text = ""
        playerBTextBox.text = ""
        playerCTextBox.text = ""
        playerDTextBox.text = ""
    }

    fun playerConfigList(playerCount: Int, type: Int): MutableList<PlayerConfig>{
        val age = 0
        val typeList: MutableList<PlayerConfig> = mutableListOf()

        var p1Name = ""
        var p2Name = ""
        var p3Name = ""
        var p4Name = ""

        var p1Type: PlayerType = PlayerType.PERSON
        var p2Type: PlayerType = PlayerType.PERSON
        var p3Type: PlayerType = PlayerType.PERSON
        var p4Type: PlayerType = PlayerType.PERSON

        if(playerCount == 2){
            p1Name = playerATextBox.text
            p2Name = playerBTextBox.text
        }
        else if(playerCount == 3){
            p1Name = playerATextBox.text
            p2Name = playerBTextBox.text
            p3Name = playerCTextBox.text
        }
        else if(playerCount == 4){
            p1Name = playerATextBox.text
            p2Name = playerBTextBox.text
            p3Name = playerCTextBox.text
            p4Name = playerDTextBox.text
        }

        if(type == 1){
            p1Type = PlayerType.COMPUTER
        }
        if(type == 2){
            p2Type = PlayerType.COMPUTER
        }
        if(type == 3){
            p3Type = PlayerType.COMPUTER
        }
        if(type == 4){
            p4Type = PlayerType.COMPUTER
        }

        return typeList
    }

    init {
        addComponents(
            returnFromNameButton,
            playerATextBox, playerBTextBox,
            playerCTextBox, playerDTextBox,
            kiButtonA, kiButtonB, kiButtonC, kiButtonD,
            playerSequenzAButton, playerSequenzBButton,
            playerSequenzCButton, playerSequenzDButton,
            labelKiSpeedPartA, labelKiSpeedPartB, kiSpeedTextField,
            playerSequenceLabel, randomCheckbox,
            startGameButton
        )
    }
}