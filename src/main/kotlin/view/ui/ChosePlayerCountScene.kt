package view.ui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.*
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual


/**
 * creates a selection menu for the different numbers of players
 */
class ChosePlayerCountScene : MenuScene(1920, 1080,
    background = ImageVisual("cecihoney-background-desert-full.jpg")) {

    private val headLabel = Label(
        width = 400, height = 100,
        posX = 760, posY = 112.5,
        text = "Select a Game mode: ",
        font = Font(size = 35),
        visual = ColorVisual(ColorEnum.Olivine.toRgbValue()). apply {
            backgroundRadius = BackgroundRadius(10)
        }
    )

    private val buttonLabel = Label(
        width = 400, height = 400,
        posX = 760, posY = 275,
        visual = ColorVisual(ColorEnum.Papaya.toRgbValue()). apply {
            backgroundRadius = BackgroundRadius(10)
        }
    )

    val p2Button = Button(
        width = 200, height = 50,
        posX = 860, posY = 300,
        text = "2 Player",
        font = Font(18),
        visual = ColorVisual(ColorEnum.Wheat.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(5)
        }
    )

    val p3SharedButton = Button(
        width = 240, height = 50,
        posX = 840, posY = 400,
        text = "3 Player (Shared Gates)",
        font = Font(18),
        visual = ColorVisual(ColorEnum.Wheat.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(5)
        }
    )

    val p3OwnButton = Button(
        width = 200, height = 50,
        posX = 860, posY = 500,
        text = "3 Player (Own Gates)",
        font = Font(18),
        visual = ColorVisual(ColorEnum.Wheat.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(5)
        }
    )


    val p4Button = Button(
        width = 200, height = 50,
        posX = 860, posY = 600,
        text = "4 Player",
        font = Font(18),
        visual = ColorVisual(ColorEnum.Wheat.toRgbValue()).apply {
                backgroundRadius = BackgroundRadius(5)
            }
    )

    val backButton = Button(width = 40, height = 40,
        posX = 1, posY = 1, visual = CompoundVisual(
            ImageVisual(
                path = "blackArrow.png"
            )
        )
    )


    init {
        opacity = 0.5
        addComponents(buttonLabel, p2Button, p3OwnButton, p3SharedButton, p4Button,
            headLabel, backButton)
    }
}