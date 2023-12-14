package view.ui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.ComboBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

class WelcomeScene : MenuScene(1920, 1080) {

    private val headLineLabel = Label(width = 750, height = 100,posX = 575, posY = 100,
        text="Indigo-Game!", font = Font(size = 100)
    )

    private val onlineGameBox = ComboBox<String>(
        posX = 800, posY = 250,
        width = 200, height = 50,
        prompt = "Online-Game ",
        items = mutableListOf("Select Host", "Select Guest")
    )

    private val hotSeatModeButton = Button(
        posX = 825, posY = 350,
        width = 125, height = 50,
        text = "Hotseat - Mode"
    ).apply {  }

    private val loadGameButton = Button(
        posX = 825, posY = 450,
        width = 125, height = 50,
        text = "load game"
    ).apply {  }

    val quitButton = Button(
    posX = 825, posY = 550,
    width = 100, height = 35,
        text = "Quit."
    ).apply { visual = ColorVisual(252, 0, 0)
    }

    init {
        opacity = 0.5
        addComponents(headLineLabel,
            onlineGameBox, hotSeatModeButton,
            loadGameButton, quitButton)
    }
}