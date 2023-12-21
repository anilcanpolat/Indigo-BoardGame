package view.ui


import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual

/**
 * creates the endgame scene of the Indigo game
 */
class EndgameScene : MenuScene(700, 1080){

    private val gold = Label(width = 30, height = 50,
        posX = 10, posY = 100).apply{
            visual = CompoundVisual(
                ImageVisual(path = "gold.png")
            )
        }

    private val silver = Label(width = 30, height = 50,
        posX = 10, posY = 250).apply{
        visual = CompoundVisual(
            ImageVisual(path = "silber.png")
        )
    }

    private val bronze = Label(width = 30, height = 50,
        posX = 10, posY = 400).apply{
        visual = CompoundVisual(
            ImageVisual(path = "bronze.png")
        )
    }

    private val fourthPlace = Label(width = 30, height = 50,
        posX = 15, posY = 550, text = "4.", font = Font(size = 35)
    )

    val quitButton = Button(
        width = 150, height = 50,
        posX = 150, posY = 700,
        text = "Quit"
    ).apply {
        visual = ColorVisual(221, 136, 136)
    }

    val startGameButton = Button(
        width = 150, height = 50,
        posX = 400, posY = 700,
        text = "New Game"
    ).apply {
        visual = ColorVisual(136, 221, 136)
    }

    private val winnerOne = Label(width = 30, height = 50,
        posX = 10, posY = 100, text = "winner 1")

    private val winnerTwo = Label(width = 30, height = 50,
        posX = 10, posY = 250, text = "winner 2")

    private val winnerThree = Label(width = 30, height = 50,
        posX = 10, posY = 400, text = "winner 3")

    private val winnerFour = Label(width = 30, height = 50,
        posX = 10, posY = 550, text = "winner 4")

    init {
        opacity = 0.5

        addComponents(gold, silver, bronze, fourthPlace, quitButton, startGameButton,
            winnerOne, winnerTwo, winnerThree, winnerFour)
    }

}