package view.ui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene

class ChosePlayerCountScene : MenuScene(1920, 1080) {
    private val p2Button = Button(
        width = 100, height = 35,
        posX = 50, posY = 125,
        text = "2 Player"
    )

    private val p3SharedButton = Button(
        width = 100, height = 35,
        posX = 50, posY = 125,
        text = "3 Player (Shared Gates)"
    )

    private val p3OwnButton = Button(
        width = 100, height = 35,
        posX = 50, posY = 125,
        text = "3 Player (Own Gates)"
    )

    private val p4Button = Button(
        width = 100, height = 35,
        posX = 50, posY = 125,
        text = "4 Player"
    )

    //private val backButton = Button(width = 100, height = 35,
    //    posX = 50, posY = 125, visual = "BlackArrow.png")


    init {
        opacity = 0.5
        addComponents(p2Button, p3OwnButton, p3SharedButton, p4Button)
    }
}