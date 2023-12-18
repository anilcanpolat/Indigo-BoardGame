package view.ui

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

    private val playerDTextBox = TextField(
        posX = 760, posY = 650,
        width = 250, height = 50,
        prompt = "Put in Name! "
    )

    // KI Buttons
    private val kiButtonA = Button(
        posX = 1035, posY = 200,
        width = 50, height = 50,
        text = "KI: "
    ).apply { visual = ColorVisual(193, 74, 240) }

    private val kiButtonB = Button(
        posX = 1035, posY = 350,
        width = 50, height = 50,
        text = "KI: "
    ).apply { visual = ColorVisual(193, 74, 240) }

    private val kiButtonC = Button(
        posX = 1035, posY = 500,
        width = 50, height = 50,
        text = "KI: "
    ).apply { visual = ColorVisual(193, 74, 240) }

    private val kiButtonD = Button(
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

    private val playerSequenzDButton = Button(
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
    )

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