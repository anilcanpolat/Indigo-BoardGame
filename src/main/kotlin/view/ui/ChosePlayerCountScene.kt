package view.ui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.BorderColor
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.style.BorderStyle
import tools.aqua.bgw.style.BorderWidth
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
        width = 300, height = 75,
        posX = 810, posY = 125,
        text = "Select a Game mode: ",
        font = Font(size = 25),
        visual = ColorVisual(ColorEnum.Olivine.toRgbValue()). apply {
            borderRadius = BorderRadius(100)
            borderWidth = BorderWidth(100)
            borderStyle = BorderStyle.SOLID
            borderColor = BorderColor.BLUE
        }
    )

    val p2Button = Button(
        width = 200, height = 50,
        posX = 860, posY = 250,
        text = "2 Player",
        font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue()) }

    val p3SharedButton = Button(
        width = 200, height = 50,
        posX = 860, posY = 350,
        text = "3 Player (Shared Gates)",
        font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue())}

    val p3OwnButton = Button(
        width = 200, height = 50,
        posX = 860, posY = 450,
        text = "3 Player (Own Gates)",
        font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue())}

    val p4Button = Button(
        width = 200, height = 50,
        posX = 860, posY = 550,
        text = "4 Player",
        font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue())}

    val backButton = Button(width = 40, height = 40,
        posX = 1, posY = 1, visual = CompoundVisual(
            ImageVisual(
                path = "blackArrow.png"
            )
        )
    )


    init {
        opacity = 0.5
        addComponents(p2Button, p3OwnButton, p3SharedButton, p4Button,
            headLabel, backButton)
    }
}