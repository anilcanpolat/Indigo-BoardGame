package view.ui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font

/**
 * creates a selection menu for the different numbers of players
 */
class ChosePlayerCountScene : MenuScene(400, 1080) {

    private val headLabel = Label(
        width = 300, height = 75,
        posX = 50, posY = 50,
        text = "Choose number of players",
        font = Font(size = 25)
    )

    private val p2Button = Button(
        width = 200, height = 50,
        posX = 100, posY = 150,
        text = "2 Player"
    )

    private val p3SharedButton = Button(
        width = 200, height = 50,
        posX = 100, posY = 250,
        text = "3 Player (Shared Gates)"
    )

    private val p3OwnButton = Button(
        width = 200, height = 50,
        posX = 100, posY = 350,
        text = "3 Player (Own Gates)"
    )

    private val p4Button = Button(
        width = 200, height = 50,
        posX = 100, posY = 450,
        text = "4 Player"
    )

    //private val backButton = Button(width = 100, height = 35,
    //    posX = 50, posY = 125, visual = "BlackArrow.png")


    init {
        opacity = 0.5
        addComponents(p2Button, p3OwnButton, p3SharedButton, p4Button,
            headLabel)
    }
}