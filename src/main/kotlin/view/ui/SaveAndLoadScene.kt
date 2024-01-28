package view.ui

import service.Refreshable
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import java.io.File

/**
 * @property returnFromSaveButton creates button to return to previous menu
 * @property labelOne - labelFour just carries a digit for the respective save state
 * @property saveButtonOne if the save Button does hold a save state it suppose to
 * say "EMPTY", then onclick it should be able to create a save state.
 * When the Button was already used to hold a save state, it says "Game from: "
 * and on Click it loads the respective save state
 * @property deleteButtonOne deletes the gameState,
 * that is saved in the respective button
 */
class SaveAndLoadScene : MenuScene(1920, 1080,
    background = ImageVisual("cecihoney-background-desert-full.jpg")), Refreshable {

    val returnFromSaveButton = Button(
        posX = 10, posY = 10,
        width = 40, height = 40,
        visual = CompoundVisual(
            ImageVisual(
                path = "blackArrow.png")
        )
    )

    private val headerLabel = Label(
        posX = 735, posY = 87.5,
        width = 500, height = 75,
        text = "Save & Load a game state: ",
        font = Font(35),
        visual = ColorVisual(ColorEnum.Olivine.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(10)
        }
    )

    private val labelOne = Label(
        posX = 715, posY = 259.25,
        width = 25, height = 25,
        text = "1: ", font = Font(20)
    )

    private val labelTwo = Label(
        posX = 715, posY = 409.25,
        width = 25, height = 25,
        text = "2: ", font = Font(20)
    )

    private val labelThree = Label(
        posX = 715, posY = 559.25,
        width = 25, height = 25,
        text = "3: ", font = Font(20)
    )

    private val labelFour = Label(
        posX = 715, posY = 709.25,
        width = 25, height = 25,
        text = "4: ", font = Font(20)
    )

    private val labelFive = Label(
        posX = 715, posY = 859.25,
        width = 25, height = 25,
        text = "5: ", font = Font(20)
    )

    val saveButtonOne = Button(
        posX = 745, posY = 246.75,
        width = 450, height = 50,
        text = "Empty: ",
        font = Font(16)
    ).apply {
        if (File("savestate1.json").exists()){
            text = "Savestate 1"
        }
    }

    val saveButtonTwo = Button(
        posX = 745, posY = 396.75,
        width = 450, height = 50,
        text = "Empty: ",
        font = Font(16)
    ).apply {
        if (File("savestate2.json").exists()){
            text = "Savestate 2"
        }
    }

    val saveButtonThree = Button(
        posX = 745, posY = 546.75,
        width = 450, height = 50,
        text = "Empty: ",
        font = Font(16)
    ).apply {
        if (File("savestate3.json").exists()){
            text = "Savestate 3"
        }
    }

    val saveButtonFour = Button(
        posX = 745, posY = 696.75,
        width = 450, height = 50,
        text = "Empty: ",
        font = Font(16)
    ).apply {
        if (File("savestate4.json").exists()){
            text = "Savestate 4"
        }
    }

    val saveButtonFive = Button(
        posX = 745, posY = 846.75,
        width = 450, height = 50,
        text = "Empty: ",
        font = Font(16)
    ).apply {
        if (File("savestate5.json").exists()){
            text = "Savestate 5"
        }
    }

    val deleteButtonOne = Button(
        posX = 1163, posY = 250,
        width = 30, height = 45,
        text = "D", font = Font(16)
    ).apply { visual = ColorVisual(193, 74, 240)}

    val deleteButtonTwo = Button(
        posX = 1163, posY = 400,
        width = 30, height = 45,
        text = "D", font = Font(16)
    ).apply { visual = ColorVisual(193, 74, 240)}

    val deleteButtonThree = Button(
        posX = 1163, posY = 550,
        width = 30, height = 45,
        text = "D", font = Font(16)
    ).apply { visual = ColorVisual(193, 74, 240)}

    val deleteButtonFour = Button(
        posX = 1163, posY = 697.5,
        width = 30, height = 47.5,
        text = "D", font = Font(16)
    ).apply { visual = ColorVisual(193, 74, 240)}

    val deleteButtonFive = Button(
        posX = 1163, posY = 850,
        width = 30, height = 45,
        text = "D", font = Font(16)
    ).apply { visual = ColorVisual(193, 74, 240)}

    private val labelDateOne = Label(
        posX = 1205, posY = 246.75,
        width = 60, height = 50,
        text = "Date: ", font = Font(16),
        visual = ColorVisual(135, 135, 135)
    )

    private val labelDateTwo = Label(
        posX = 1205, posY = 396.75,
        width = 60, height = 50,
        text = "Date: ", font = Font(16),
        visual = ColorVisual(135, 135, 135)
    )

    private val labelDateThree = Label(
        posX = 1205, posY = 546.75,
        width = 60, height = 50,
        text = "Date: ", font = Font(16),
        visual = ColorVisual(135, 135, 135)
    )

    private val labelDateFour = Label(
        posX = 1205, posY = 696,
        width = 60, height = 50,
        text = "Date: ", font = Font(16),
        visual = ColorVisual(135, 135, 135)
    )

    private val labelDateFive = Label(
        posX = 1205, posY = 849,
        width = 60, height = 50,
        text = "Date: ", font = Font(16),
        visual = ColorVisual(135, 135, 135)
    )

    init {
        addComponents(
            returnFromSaveButton,
            headerLabel,
            labelOne, labelTwo,
            labelThree, labelFour,
            saveButtonOne, saveButtonTwo,
            saveButtonThree, saveButtonFour,
            deleteButtonOne, deleteButtonTwo,
            deleteButtonThree, deleteButtonFour,
            labelDateOne, labelDateTwo,
            labelDateThree, labelDateFour, labelFive,
            saveButtonFive, deleteButtonFive,
            labelDateFive)
    }
}