package view.ui

import tools.aqua.bgw.components.container.HexagonGrid
import tools.aqua.bgw.components.gamecomponentviews.HexagonView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import kotlin.math.absoluteValue

/**
 * show the game filed and all of the user UI,
 * take inputs from the User and show results
 */
class GameScene : BoardGameScene(1920, 1080) {

    private val saveButton =  Button(
        width = 65, height = 65,
        posX = 1700, posY = 50,

    ).apply {
        visual = CompoundVisual(
            ColorVisual.GREEN,
            ImageVisual(path = "save.png")
        )
    }

    private val quitButton = Button(
        width = 65, height = 65,
        posX = 1800, posY = 50,
    ).apply {
        visual = CompoundVisual(
            ColorVisual(221, 136, 136),
            ImageVisual(path = "exit.png"))
    }

    private val undoButton = Button(
        width = 75, height = 75,
        posX = 1800, posY = 500,
    ).apply {
        visual = CompoundVisual(
            ImageVisual(path = "undo.png"))
    }

    private val redoButton = Button(
        width = 70, height = 70,
        posX = 1800, posY = 600,
    ).apply {
        visual = CompoundVisual(
            ImageVisual(path = "redo.png"))
    }

    private val rotateButton = Button(
        width = 100, height = 50,
        posX = 1700, posY = 900, visual = ColorVisual.GRAY, text = "Rotate"
    )


    private val hexagonGrid = HexagonGrid<HexagonView>(
         coordinateSystem = HexagonGrid.CoordinateSystem.AXIAL, posX = 900, posY = 500
    ).apply { rotate(30) }

    private val playersTile = HexagonView(
        posX = 1500, posY = 900, size =50, visual = ColorVisual(250,240,202)
    )



    init {
        for (row in -4..4) {
            for (col in -4..4) {
                if ((row + col).absoluteValue >= 5) {
                    continue
                }

                val hexagon = HexagonView(visual = ColorVisual(250,240,202 ), size = 50)
                hexagonGrid[col, row] = hexagon
            }
        }
        placeTiles()
        background = ColorVisual.LIGHT_GRAY
        addComponents(saveButton, quitButton,redoButton,undoButton, hexagonGrid, rotateButton, playersTile)
    }

    private fun placeTiles(){
        hexagonGrid[0,-4]!!.apply { visual = ColorVisual.BLUE }
        hexagonGrid[0,4]!!.apply { visual = ColorVisual.BLUE }
        hexagonGrid[-4,4]!!.apply { visual = ColorVisual.BLUE }
        hexagonGrid[-4,0]!!.apply { visual = ColorVisual.BLUE }
        hexagonGrid[4,0]!!.apply { visual = ColorVisual.BLUE }
        hexagonGrid[4,-4]!!.apply { visual = ColorVisual.BLUE }
        hexagonGrid[0,0]!!.apply { visual = ColorVisual.GREEN }

    }
}