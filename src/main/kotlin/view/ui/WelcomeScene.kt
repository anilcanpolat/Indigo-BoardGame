package view.ui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import view.IndigoApplication

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

    private val labelOnlineGame = Label(
        posX = 860, posY = 250,
        width = 200, height = 50,
        text = "Online-Game"
    ).apply { visual = ColorVisual(254, 238, 212) }

    private val hostButton = Button(
        posX = 860, posY = 310,
        width = 200, height = 50,
        text = "Play as Host: "
    ).apply { visual = ColorVisual(232, 209, 165) }

    private val guestButton = Button(
        posX = 860, 370,
        200, 50,
        text = "Play as Guest: "
    ).apply { visual = ColorVisual(232, 209, 165)
    isDisabled = true}

    private val guestIdFiled = TextField(
        posX = 885, 440,
        150, 30,
        prompt = "Enter id: "
    ).apply { visual = ColorVisual(232, 209, 165)
    onKeyTyped = {
            guestButton.isDisabled = false
            }
    }

    val hotSeatModeButton = Button(
        posX = 860, posY = 550,
        width = 200, height = 50,
        text = "Hotseat - Mode"
    ).apply { visual = ColorVisual(157, 189, 137) }

    val loadGameButton = Button(
        posX = 860, posY = 650,
        width = 200, height = 50,
        text = "load game"
    ).apply { visual = ColorVisual(157, 189, 137) }

    val quitButton = Button(
    posX = 910, posY = 750,
    width = 100, height = 35,
        text = "Quit." //, font = Font(color = Color.WHITE)
    ).apply { visual = ColorVisual(204, 20, 0)
    }

    init {
        opacity = 0.5
        addComponents(headLineLabel,
            labelOnlineGame, hostButton,
            guestButton, guestIdFiled,
            hotSeatModeButton,
            loadGameButton, quitButton)
    }
}