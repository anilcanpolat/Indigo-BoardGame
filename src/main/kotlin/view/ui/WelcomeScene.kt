package view.ui

import service.Refreshable
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.BorderColor
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.style.BorderStyle
import tools.aqua.bgw.style.BorderWidth
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 * Scene that welcomes the player in and gives the option to play online through
 * the network or in the hotseat mode. Also allows the player to load a save state
 * and quit the game.
 */
class WelcomeScene : MenuScene(1920, 1080,
    background = ImageVisual("cecihoney-background-desert-full.jpg")), Refreshable {

    private val headLineLabel = Label(width = 750, height = 100,posX = 575, posY = 100,
        text="Indigo-Game!", font = Font(size = 100)
    )

    private val labelOnlineGame = Label(
        posX = 810, posY = 250,
        width = 300, height = 50,
        text = "Play a network game: ",
        font = Font(25)
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue())}

    val hostButton = Button(
        posX = 860, posY = 310,
        width = 200, height = 50,
        text = "Play as Host",
        font = Font(20, Color.BLACK, "Open Sans")
    ).apply { visual = ColorVisual(ColorEnum.Wheat.toRgbValue()) }

    val guestButton = Button(
        posX = 860, 370,
        200, 50,
        text = "Play as Guest",
        font = Font(20, Color.BLACK, fontStyle = Font.FontStyle.ITALIC),
        visual = ColorVisual(ColorEnum.Wheat.toRgbValue()).apply {
            borderRadius = BorderRadius(5)
            borderWidth = BorderWidth(2)
            borderColor = BorderColor(ColorEnum.EngOrange.toRgbValue())
        }
    )


    val hotSeatModeButton = Button(
        posX = 860, posY = 550,
        width = 200, height = 50,
        text = "Hotseat - Mode",
        font = Font(20, Color.BLACK)
    ).apply { visual = ColorVisual(ColorEnum.Olivine.toRgbValue()) }

    val loadGameButton = Button(
        posX = 860, posY = 650,
        width = 200, height = 50,
        text = "load game",
        font = Font(20)
    ).apply {
        visual = ColorVisual(ColorEnum.Olivine.toRgbValue())
    }


    val quitButton = Button(
    posX = 910, posY = 750,
    width = 100, height = 35,
        text = "Quit.", font = Font(20)
    ).apply { visual = ColorVisual(ColorEnum.EngOrange.toRgbValue()).apply {
                borderStyle = BorderStyle.DOTTED
                borderWidth = BorderWidth(100)
                borderColor = BorderColor(Color.WHITE)
        }
    }

    init {
        opacity = 0.5
        addComponents(headLineLabel,
            labelOnlineGame, hostButton,
            guestButton, hotSeatModeButton,
            loadGameButton, quitButton)
    }
}