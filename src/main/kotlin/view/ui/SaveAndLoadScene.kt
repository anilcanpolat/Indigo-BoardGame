package view.ui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual

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
    background = ImageVisual("cecihoney-background-desert-full.jpg")) {

    val returnFromSaveButton = Button(
        posX = 10, posY = 10,
        width = 40, height = 40,
        visual = CompoundVisual(
            ImageVisual(
                path = "blackArrow.png")
        )
    )


    private val labelOne = Label(
        posX = 715, posY = 100,
        width = 25, height = 25,
        text = "1: "
    )

    private val labelTwo = Label(
        posX = 715, posY = 250,
        width = 25, height = 25,
        text = "2: "
    )

    private val labelThree = Label(
        posX = 715, posY = 400,
        width = 25, height = 25,
        text = "3: "
    )

    private val labelFour = Label(
        posX = 715, posY = 550,
        width = 25, height = 25,
        text = "4: "
    )

    private val saveButtonOne = Button(
        posX = 745, posY = 87.5,
        width = 450, height = 50,
        text = "Game from: "
    )

    private val saveButtonTwo = Button(
        posX = 745, posY = 240,
        width = 450, height = 50,
        text = "Game from: "
    )

    private val saveButtonThree = Button(
        posX = 745, posY = 388.5,
        width = 450, height = 50,
        text = "Empty: "
    )

    private val saveButtonFour = Button(
        posX = 745, posY = 538.5,
        width = 450, height = 50,
        text = "Empty: "
    )

    private val deleteButtonOne = Button(
        posX = 1163, posY = 90,
        width = 30, height = 45,
        text = "D"
    ).apply { visual = ColorVisual(193, 74, 240)}

    private val deleteButtonTwo = Button(
        posX = 1163, posY = 242.5,
        width = 30, height = 45,
        text = "D"
    ).apply { visual = ColorVisual(193, 74, 240)}

    private val deleteButtonThree = Button(
        posX = 1163, posY = 390,
        width = 30, height = 45,
        text = "D"
    ).apply { visual = ColorVisual(193, 74, 240)}

    private val deleteButtonFour = Button(
        posX = 1163, posY = 540,
        width = 30, height = 47.5,
        text = "D"
    ).apply { visual = ColorVisual(193, 74, 240)}

    private val labelDateOne = Label(
        posX = 1205, posY = 87.5,
        width = 60, height = 50,
        text = "Date: ",
        visual = ColorVisual(135, 135, 135)
    )

    private val labelDateTwo = Label(
        posX = 1205, posY = 240,
        width = 60, height = 50,
        text = "Date: ",
        visual = ColorVisual(135, 135, 135)
    )

    private val labelDateThree = Label(
        posX = 1205, posY = 388.5,
        width = 60, height = 50,
        text = "Date: ",
        visual = ColorVisual(135, 135, 135)
    )

    private val labelDateFour = Label(
        posX = 1205, posY = 538.5,
        width = 60, height = 50,
        text = "Date: ",
        visual = ColorVisual(135, 135, 135)
    )

    init {
        addComponents(
            returnFromSaveButton,
            labelOne, labelTwo,
            labelThree, labelFour,
            saveButtonOne, saveButtonTwo,
            saveButtonThree, saveButtonFour,
            deleteButtonOne, deleteButtonTwo,
            deleteButtonThree, deleteButtonFour,
            labelDateOne, labelDateTwo,
            labelDateThree, labelDateFour)
    }
}