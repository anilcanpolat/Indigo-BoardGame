package view.ui

import entity.PlayerConfig
import entity.PlayerType
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.CheckBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual

/**
 * scene that allows the player to input there name and other utilities
 * required for the game to start. only shows properties that are required for
 * the amount of players selected. also gives us control over KI.
 */
class SelectNameAndKiScene : MenuScene(1920, 1080,
    background = ImageVisual("cecihoney-background-desert-full.jpg")) {

    val returnFromNameButton = Button(
        posX = 10, posY = 10,
        width = 40, height = 40,
        visual = CompoundVisual(
            ImageVisual(
                path = "blackArrow.png")
        )
    )

    private val headerLabel = Label(
        posX = 710, posY = 45,
        width = 500, height = 95,
        text = "Choose your settings", font = Font(35),
        visual = ColorVisual(ColorEnum.Wheat.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(10)
        }
    )


    //textfield for player names
    private val playerATextBox = TextField(
        posX = 760, posY = 200,
        width = 250, height = 50,
        font = Font(16),
        prompt = "Put in Name! "
    ).apply {
        onKeyTyped = {
            startGameButton.isDisabled = !checkIfBlank()
        }
    }

    private val playerBTextBox = TextField(
        posX = 760, posY = 350,
        width = 250, height = 50,
        prompt = "Put in Name! ",
        font = Font(16)
    ).apply {
        onKeyTyped = {
            startGameButton.isDisabled = !checkIfBlank()
        }
    }

    private val playerCTextBox = TextField(
        posX = 760, posY = 500,
        width = 250, height = 50,
        prompt = "Put in Name! ",
        font = Font(16)
    ).apply {
        onKeyTyped = {
            startGameButton.isDisabled = !checkIfBlank()
        }
    }

    private val playerDTextBox = TextField(
        posX = 760, posY = 650,
        width = 250, height = 50,
        prompt = "Put in Name! ",
        font = Font(16)
    ).apply {
        onKeyTyped = {
            startGameButton.isDisabled = !checkIfBlank()
        }
    }

    // KI Buttons
    private val kiButtonA = Button(
        posX = 1030, posY = 200,
        width = 70, height = 50,
        text = "Add Ki",
        font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue())
         onMouseClicked = {
             kiLevelA++
             kiA = setKILevel(kiLevelA)
             playerATextBox.text = printOrDeleteKiNames(kiA, kiLevelA)
             visual = changeColorForKi(kiA, kiLevelA)

        }
    }

    private val kiButtonB = Button(
        posX = 1030, posY = 350,
        width = 70, height = 50,
        text = "Add Ki",
        font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue())
        onMouseClicked = {
            kiLevelB++
            kiB = setKILevel(kiLevelB)
            playerBTextBox.text = printOrDeleteKiNames(kiB, kiLevelB)
            visual = changeColorForKi(kiB, kiLevelB)
        }
    }

    private val kiButtonC = Button(
        posX = 1030, posY = 500,
        width = 70, height = 50,
        text = "Add Ki",
        font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue())
        onMouseClicked = {
            kiLevelC++
            kiC = setKILevel(kiLevelC)
            playerCTextBox.text = printOrDeleteKiNames(kiC, kiLevelC)
            visual = changeColorForKi(kiC, kiLevelC)
        }
    }

    private val kiButtonD = Button(
        posX = 1030, posY = 650,
        width = 70, height = 50,
        text = "Add Ki",
        font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue())
        onMouseClicked = {
            kiLevelD++
            kiD = setKILevel(kiLevelD)
            playerDTextBox.text = printOrDeleteKiNames(kiD, kiLevelD)
            visual = changeColorForKi(kiD, kiLevelD)
        }
    }

    //var for level of Ki
    private var kiLevelA = 0
    private var kiLevelB = 0
    private var kiLevelC = 0
    private var kiLevelD = 0

    var kiA : Boolean = false
    var kiB : Boolean = false
    var kiC : Boolean = false
    var kiD : Boolean = false

    //SequenzButtons
    private val playerSequenzAButton = Button(
        posX = 1110, posY = 200,
        width = 50, height = 50,
        text = "1: ",
        font = Font(20)
    ).apply { visual = ColorVisual(90, 74, 240)
    onMouseClicked = {
            playerAPos = cycleThroughPlayerSequence(playerAPos)
            text = playerAPos.toString()
        }
    }

    private val playerSequenzBButton = Button(
        posX = 1110, posY = 350,
        width = 50, height = 50,
        text = "2: ",
        font = Font(20)
    ).apply { visual = ColorVisual(90, 74, 240)
        onMouseClicked = {
            playerBPos = cycleThroughPlayerSequence(playerBPos)
            text = playerBPos.toString()
        }
    }

    private val playerSequenzCButton = Button(
        posX = 1110, posY = 500,
        width = 50, height = 50,
        text = "3: ",
        font = Font(20)
    ).apply { visual = ColorVisual(90, 74, 240)
        onMouseClicked = {
            playerCPos = cycleThroughPlayerSequence(playerCPos)
            text = playerCPos.toString()
        }
    }

    private val playerSequenzDButton = Button(
        posX = 1110, posY = 650,
        width = 50, height = 50,
        text = "4: ",
        font = Font(20)
    ).apply { visual = ColorVisual(90, 74, 240)
        onMouseClicked = {
            playerDPos = cycleThroughPlayerSequence(playerDPos)
            text = playerDPos.toString()
        }
    }

    //var for sequence
    private var overAllPosArray : BooleanArray = BooleanArray(5)
    private var overAllPos = 0
    private var playerAPos = 0
    private var playerBPos = 0
    private var playerCPos = 0
    private var playerDPos = 0


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


    val startGameButton = Button(
        posX = 955, posY = 790,
        width = 200, height = 100,
        text = "Start!", font = Font(50)
    ).apply { visual = ColorVisual(ColorEnum.Olivine.toRgbValue())
    isDisabled = true}


    //functions
    private fun checkIfBlank() : Boolean {
        var checkBlank = false
        if(!playerDTextBox.isVisible){
            checkBlank = (playerATextBox.text.isNotBlank() &&
                    playerBTextBox.text.isNotBlank() &&
                    playerCTextBox.text.isNotBlank())

        }

        if(!playerDTextBox.isVisible && !playerCTextBox.isVisible){
            checkBlank = (playerATextBox.text.isNotBlank() &&
                    playerBTextBox.text.isNotBlank())
        }

        if(playerDTextBox.isVisible) {
            checkBlank = (playerATextBox.text.isNotBlank() &&
                    playerBTextBox.text.isNotBlank() &&
                    playerCTextBox.text.isNotBlank() &&
                    playerDTextBox.text.isNotBlank())
        }

        return checkBlank
    }

    private fun setKILevel(level : Int) : Boolean{
        var isKi = false
        when (level % 3){
            0 -> { isKi = false}
            1 -> { isKi = true}
            2 -> { isKi = true}
        }
        return isKi
    }

    private fun printOrDeleteKiNames(kiForName : Boolean, kiLevelForName : Int) : String{
        var str = ""
        if(kiForName){
            if(kiLevelForName % 3 == 1){
                str = "[G4]RandomKi"
            }
            if(kiLevelForName % 3 == 2){
                str = "[G4]MurderProKiLevel500"
            }
        } else {
            str = ""
        }
        return str
    }

    private fun changeColorForKi(kiForName : Boolean, kiLevelForName : Int) : ColorVisual{
        var colorToReturn = ColorVisual(ColorEnum.Papaya.toRgbValue())
        if(kiForName){
            if(kiLevelForName % 3 == 1){
                colorToReturn = ColorVisual(ColorEnum.Olivine.toRgbValue())
            }
            if(kiLevelForName % 3 == 2){
                colorToReturn = ColorVisual(ColorEnum.EngOrange.toRgbValue())
            }
        } else {
            colorToReturn = ColorVisual(ColorEnum.Papaya.toRgbValue())
        }
        return colorToReturn
    }

    private fun cycleThroughPlayerSequence(pos : Int) : Int{
        var posToReturn = 0
        if(pos == 0){
            for(i in 1 .. 4){
                if(!overAllPosArray[i]){
                    overAllPosArray[i] = true
                    posToReturn = i
                    overAllPos++
                    break
                }
            }
        } else{
            overAllPosArray[pos] = false
            overAllPos--
            posToReturn = 0
        }
        return posToReturn
    }

    /**
     * a function to set the Scenes for the amount of players selected by the player.
     * this means we only show buttons required and hide the others.
     */
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

    private fun setKIDelay() : Int{
        val delay = try {
            Integer.parseInt(kiSpeedTextField.text, 10)
        } catch (e: NumberFormatException) {
            println("Invalid speed value. Defaulting to 250ms!")
            250
        }
        return delay
    }

    /**
     * function to reset this Scene when pressing the return button.
     */
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

        startGameButton.isDisabled = true
    }

    private fun sequenceTheListForReturn(playerCount: Int, p1 : PlayerConfig,
                                         p2 : PlayerConfig,
                                         p3 : PlayerConfig,
                                         p4 : PlayerConfig) : MutableList<PlayerConfig>{

        val typeList: MutableList<PlayerConfig>
        val returnTypeList : MutableList<PlayerConfig> = mutableListOf()

        if(overAllPos == 0) {
            when (playerCount) {
                1 -> {
                    typeList = MutableList(2) { PlayerConfig("alex", 0, PlayerType.PERSON) }
                    typeList.clear()
                    typeList.add(p1)
                    typeList.add(p2)
                    returnTypeList.addAll(typeList)
                }
                2, 3 -> {
                    typeList = MutableList(3) { PlayerConfig("alex", 0, PlayerType.PERSON) }
                    typeList.clear()
                    typeList.add(p1)
                    typeList.add(p2)
                    typeList.add(p3)
                    returnTypeList.addAll(typeList)
                }
                4 -> {
                    typeList = MutableList(4) { PlayerConfig("alex", 0, PlayerType.PERSON) }
                    typeList.clear()
                    typeList.add(p1)
                    typeList.add(p2)
                    typeList.add(p3)
                    typeList.add(p4)
                    returnTypeList.addAll(typeList)
                }
            }
        } else {
            when (playerCount) {
                1 -> {
                    typeList = MutableList(2) { PlayerConfig("alex", 0, PlayerType.PERSON) }
                    typeList.removeAt(playerAPos-1)
                    typeList.add(playerAPos-1, p1)
                    typeList.removeAt(playerBPos-1)
                    typeList.add(playerBPos-1, p2)
                    returnTypeList.addAll(typeList)
                }
                2, 3 -> {
                    typeList = MutableList(3) { PlayerConfig("alex", 0, PlayerType.PERSON) }
                    typeList.removeAt(playerAPos-1)
                    typeList.add(playerAPos-1, p1)
                    typeList.removeAt(playerBPos-1)
                    typeList.add(playerBPos-1, p2)
                    typeList.removeAt(playerCPos-1)
                    typeList.add(playerCPos-1, p3)
                    returnTypeList.addAll(typeList)
                }
                4 -> {
                    typeList = MutableList(4) { PlayerConfig("alex", 0, PlayerType.PERSON) }
                    typeList.removeAt(playerAPos-1)
                    typeList.add(playerAPos-1, p1)
                    typeList.removeAt(playerBPos-1)
                    typeList.add(playerBPos-1, p2)
                    typeList.removeAt(playerCPos-1)
                    typeList.add(playerCPos-1, p3)
                    typeList.removeAt(playerDPos-1)
                    typeList.add(playerDPos-1, p4)
                    returnTypeList.addAll(typeList)
                }
            }
        }

        if(randomCheckbox.isChecked) {
            returnTypeList.shuffle()
        }
        return returnTypeList
    }

    /**
     *  playerConfig is created and used to give data to service layer when
     *  the start game Button is pressed.
     */
    fun playerConfigList(playerCount: Int,
                         kiA: Boolean,
                         kiB: Boolean,
                         kiC: Boolean,
                         kiD: Boolean): MutableList<PlayerConfig>{

        var p1Name = ""
        var p2Name = ""
        var p3Name = ""
        var p4Name = ""

        var p1Type: PlayerType = PlayerType.PERSON
        var p2Type: PlayerType = PlayerType.PERSON
        var p3Type: PlayerType = PlayerType.PERSON
        var p4Type: PlayerType = PlayerType.PERSON

        var p1IsRandomKi = false
        var p2IsRandomKi = false
        var p3IsRandomKi = false
        var p4IsRandomKi = false

        var p1KiSpeed = 250
        var p2KiSpeed = 250
        var p3KiSpeed = 250
        var p4KiSpeed = 250

        when (playerCount) {
            1 -> {
                p1Name = playerATextBox.text
                p2Name = playerBTextBox.text
            }
            2, 3 -> {
                p1Name = playerATextBox.text
                p2Name = playerBTextBox.text
                p3Name = playerCTextBox.text
            }
            4 -> {
                p1Name = playerATextBox.text
                p2Name = playerBTextBox.text
                p3Name = playerCTextBox.text
                p4Name = playerDTextBox.text
            }
        }

        if(kiA){
            p1Type = PlayerType.COMPUTER
             if(kiLevelA == 1){p1IsRandomKi = true}
             p1KiSpeed = setKIDelay()
        }
        if(kiB){
            p2Type = PlayerType.COMPUTER
            if(kiLevelB == 1){p2IsRandomKi = true}
            p2KiSpeed = setKIDelay()
        }
        if(kiC){
            p3Type = PlayerType.COMPUTER
            if(kiLevelC == 1){p3IsRandomKi = true}
            p3KiSpeed = setKIDelay()
        }
        if(kiD){
            p4Type = PlayerType.COMPUTER
            if(kiLevelC == 1){p4IsRandomKi = true}
            p4KiSpeed = setKIDelay()
        }

        val p1 = PlayerConfig(p1Name, 0, p1Type, p1IsRandomKi, p1KiSpeed)
        val p2 = PlayerConfig(p2Name, 0, p2Type, p2IsRandomKi, p2KiSpeed)
        val p3 = PlayerConfig(p3Name, 0, p3Type, p3IsRandomKi, p3KiSpeed)
        val p4 = PlayerConfig(p4Name, 0, p4Type, p4IsRandomKi, p4KiSpeed)


        var finalTypeList : MutableList<PlayerConfig> = mutableListOf()
        finalTypeList = sequenceTheListForReturn(playerCount,p1, p2, p3, p4)
        return finalTypeList
    }

    init {
        addComponents(
            returnFromNameButton, headerLabel,
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