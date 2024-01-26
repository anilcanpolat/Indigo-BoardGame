package view.ui

import service.Refreshable
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.*
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
        font = Font(25),
        visual = ColorVisual(ColorEnum.Papaya.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(10)
        }
    )

    private val networkLabel = Label(
        posX = 810, posY = 250,
        width = 300, height = 190,
        visual = ColorVisual(ColorEnum.Papaya.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(20)
        }
    )

    private val hostGameLabel = Label(
        posX = 810, posY = 480,
        width = 300, height = 100,
        visual = ColorVisual(ColorEnum.Papaya.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(20)
        }
    )

    private val loadLabel = Label(
        posX = 810, posY = 625,
        width = 300, height = 100,
        visual = ColorVisual(ColorEnum.Papaya.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(20)
        }
    )

    val hostButton = Button(
        posX = 860, posY = 310,
        width = 200, height = 50,
        text = "Play as Host",
        font = Font(20, Color.BLACK),
        visual = ColorVisual(ColorEnum.Wheat.toRgbValue()). apply {
            backgroundRadius = BackgroundRadius(15)
        }
    )

    val guestButton = Button(
        posX = 860, 370,
        200, 50,
        text = "Play as Guest",
        font = Font(20, Color.BLACK),
        visual = ColorVisual(ColorEnum.Wheat.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(15)
        }
    )


    val hotSeatModeButton = Button(
        posX = 860, posY = 505,
        width = 200, height = 50,
        text = "Hotseat - Mode",
        font = Font(20, Color.BLACK),
        visual = ColorVisual(ColorEnum.Olivine.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(15)
        }
    )


    val loadGameButton = Button(
        posX = 860, posY = 650,
        width = 200, height = 50,
        text = "load game",
        font = Font(20),
        visual = ColorVisual(ColorEnum.Olivine.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(15)
        }
    )


    val quitButton = Button(
    posX = 910, posY = 780,
    width = 100, height = 40,
        text = "Quit.", font = Font(20)
    ).apply { visual = ColorVisual(ColorEnum.EngOrange.toRgbValue()).apply {
                backgroundRadius = BackgroundRadius(80)
        }
    }

    init {
        opacity = 0.5
        addComponents(headLineLabel, networkLabel,
            labelOnlineGame, hostButton, hostGameLabel,
            guestButton, hotSeatModeButton, loadLabel,
            loadGameButton, quitButton)
    }
}