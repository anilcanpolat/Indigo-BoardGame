package view.ui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.ComboBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual

/**
 * Scene that welcomes the player in and gives the option to play online through
 * the network or in the hotseat mode. Also allows the player to load a save state
 * and quit the game.
 */
class WelcomeScene : MenuScene(1920, 1080,
    background = ImageVisual("cecihoney-background-desert-full.jpg")) {

    private val headLineLabel = Label(width = 750, height = 100,posX = 575, posY = 100,
        text="Indigo-Game!", font = Font(size = 100)
    )

    private val onlineGameBox = ComboBox<String>(
        posX = 800, posY = 250,
        width = 200, height = 50,
        prompt = "Online-Game ",
        items = mutableListOf("Select Host", "Select Guest")
    ).apply { visual = ColorVisual(232, 209, 165) }

    private val hotSeatModeButton = Button(
        posX = 825, posY = 350,
        width = 125, height = 50,
        text = "Hotseat - Mode"
    ).apply { visual = ColorVisual(232, 209, 165) }

    private val loadGameButton = Button(
        posX = 825, posY = 450,
        width = 125, height = 50,
        text = "load game"
    ).apply { visual = ColorVisual(232, 209, 165) }

    val quitButton = Button(
    posX = 825, posY = 550,
    width = 100, height = 35,
        text = "Quit." //, font = Font(color = Color.WHITE)
    ).apply { visual = ColorVisual(204, 20, 0)
    }

    init {
        opacity = 0.5
        addComponents(headLineLabel,
            onlineGameBox, hotSeatModeButton,
            loadGameButton, quitButton)
    }
}