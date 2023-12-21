package view.ui

import entity.*
import service.Refreshable
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
class GameScene : BoardGameScene(1920, 1080),Refreshable {

    private val saveButton =  Button(
        width = 65, height = 65,
        posX = 150, posY = 50,

    ).apply {
        visual = CompoundVisual(
            ColorVisual.GREEN,
            ImageVisual(path = "save.png")
        )
    }

    private val quitButton = Button(
        width = 65, height = 65,
        posX = 50, posY = 50,
    ).apply {
        visual = ImageVisual(path = "blackArrow.png")
    }

    private val undoButton = Button(
        width = 65, height = 65,
        posX = 250, posY = 50,
    ).apply {
        visual = CompoundVisual(
            ImageVisual(path = "undo.png"))
    }

    private val redoButton = Button(
        width = 65, height = 65,
        posX = 350, posY = 50,
    ).apply {
        visual = CompoundVisual(
            ImageVisual(path = "redo.png"))
    }

    private val rotateLeftButton = Button(
        width = 130, height = 50,
        posX = 100, posY = 900, visual = ColorVisual.GRAY, text = "Rotate Left"
    ).apply { onMouseClicked={
        playersTile.rotate(-60)
    } }

    private val rotateRightButton = Button(
        width = 130, height = 50,
        posX = 100, posY = 950, visual = ColorVisual.GRAY, text = "Rotate Right"
    ).apply { onMouseClicked={
        playersTile.rotate(60)
    } }

    private val hexagonGrid = HexagonGrid<HexagonView>(
         coordinateSystem = HexagonGrid.CoordinateSystem.AXIAL, posX = 900, posY = 450
    ).apply { rotate(30) }

    private val playersTile = HexagonView(
        posX = 100, posY = 750, size =65, visual = ImageVisual(path = "longCurveTile.png")
    )

    private val routeStack = HexagonView(
        posX = 100, posY = 500, size =65, visual = CompoundVisual(
            ColorVisual(250,240,202),ImageVisual(path = "BacksideTile.png"))
    )



    init {
        //There should not be a coordinate like [4,1], [-4,-1] or [-4,-4] and others
        for (row in -4..4) {
            for (col in -4..4) {
                if ((row + col).absoluteValue >= 5) {
                    continue
                }

                val hexagon = HexagonView(visual = ColorVisual(250,240,202 ), size = 65)
                hexagonGrid[col, row] = hexagon
            }
        }
        placeTiles()
        background = ColorVisual.LIGHT_GRAY
        addComponents(saveButton, quitButton,redoButton,undoButton,
            hexagonGrid, rotateLeftButton,rotateRightButton, playersTile, routeStack)
    }



    private fun placeTiles(){
        //up
        hexagonGrid[0,-4]?.apply { visual = ImageVisual(path = "TreasureTileOutside.png").apply { rotate(240) } }
        //down
        hexagonGrid[0,4]?.apply { visual = ImageVisual(path = "TreasureTileOutside.png").apply { rotate(60) }}
        //down left
        hexagonGrid[-4,4]!!.apply { visual = ImageVisual(path = "TreasureTileOutside.png").apply { rotate(120) }}
        //up left
        hexagonGrid[-4,0]!!.apply { visual = ImageVisual(path = "TreasureTileOutside.png").apply { rotate(180) } }
        //down right
        hexagonGrid[4,0]!!.apply { visual = ImageVisual(path = "TreasureTileOutside.png") }
        //up right
        hexagonGrid[4,-4]!!.apply { visual = ImageVisual(path = "TreasureTileOutside.png").apply { rotate(-60) } }
        //center
        hexagonGrid[0,0]!!.apply { visual = ImageVisual(path = "TreasureTileInside.png") }

    }

}