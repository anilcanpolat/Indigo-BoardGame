package view.ui



import service.Refreshable
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual


/**
 * creates the endgame scene of the Indigo game
 */
class EndgameScene : MenuScene(1920, 1080,
    background = ImageVisual("cecihoney-background-desert-full.jpg")), Refreshable{



    private val gold = Label(width = 30, height = 50,
        posX = 935, posY = 50).apply{
            visual = CompoundVisual(
                ImageVisual(path = "gold.png")
            )
        }

    private val silver = Label(width = 30, height = 50,
        posX = 535, posY = 150).apply{
        visual = CompoundVisual(
            ImageVisual(path = "silber.png")
        )
    }

    val bronze = Label(width = 30, height = 50,
        posX = 1335, posY = 250).apply{
        visual = CompoundVisual(
            ImageVisual(path = "bronze.png")
        )
    }

    val fourthPlace = Label(width = 30, height = 50,
        posX = 405, posY = 700, text = "4.", font = Font(size = 35)
    )

    val quitButton = Button(
        width = 100, height = 50,
        posX = 695, posY = 850,
        text = "Quit", font = Font(16),
        visual = ColorVisual(ColorEnum.EngOrange.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(20)
        }
    )

    val startGameButton = Button(
        width = 150, height = 50,
        posX = 1050, posY = 850,
        text = "New Game", font = Font(16),
        visual = ColorVisual(ColorEnum.Olivine.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(10)
        }
    ).apply { onMouseClicked = {
        winnerTwo.isVisible = true
        winnerThree.isVisible = true
        winnerFour.isVisible = true
        fourthPlace.isVisible = true
        bronze.isVisible = true
        }
    }

    val winnerOne = Label(width = 200, height = 500,
        posX = 845, posY = 100,
        text = "", font = Font(30),
        visual = ColorVisual(255,215,0).apply {
                backgroundRadius = BackgroundRadius(10)
            }
    )

    val winnerTwo = Label(width = 200, height = 400,
        posX = 445, posY = 200,text = "winner 2",
            font = Font(30),
        visual = ColorVisual(192,192,192).apply {
            backgroundRadius = BackgroundRadius(10)
            }
        )

    val winnerThree = Label(width = 200, height = 300,
        posX = 1245, posY = 300,
        text = "winner 3", font = Font(30),
        visual = ColorVisual(205,127,50).apply {
            backgroundRadius = BackgroundRadius(10)
        }
    )

    val winnerFour = Label(width = 1000, height = 125,
        posX = 445, posY = 650,
        text = "winner 4", font = Font(30),
        visual = ColorVisual(ColorEnum.Wheat.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(10)
        }
    )

    init {
        opacity = 1.0

        addComponents(gold, silver, bronze, fourthPlace, quitButton, startGameButton,
            winnerOne, winnerTwo, winnerThree, winnerFour)
    }

}