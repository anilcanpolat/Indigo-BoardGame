package view.ui


import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual

class EndgameScene : MenuScene(700, 1080){

    private val gold = Label(width = 40, height = 70,
        posX = 10, posY = 100).apply{
            visual = CompoundVisual(
                ImageVisual(path = "gold.png")
            )
        }

    private val silver = Label(width = 40, height = 70,
        posX = 10, posY = 250).apply{
        visual = CompoundVisual(
            ImageVisual(path = "silber.png")
        )
    }

    private val bronze = Label(width = 40, height = 70,
        posX = 10, posY = 400).apply{
        visual = CompoundVisual(
            ImageVisual(path = "bronze.png")
        )
    }

    private val fourthPlace = Label(width = 40, height = 70,
        posX = 15, posY = 550, text = "4.", font = Font(size = 40)
    )

    init {
        opacity = 0.5

        addComponents(gold, silver, bronze, fourthPlace)
    }

}