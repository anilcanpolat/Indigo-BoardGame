package view.ui

import entity.Player
import entity.PlayerToken
import service.Refreshable
import tools.aqua.bgw.components.ComponentView
import tools.aqua.bgw.components.container.HexagonGrid
import tools.aqua.bgw.components.gamecomponentviews.HexagonView
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import kotlin.math.absoluteValue

/**
 * show the game filed and all the user UI,
 * take inputs from the User and show results
 */
class GameScene : BoardGameScene(1920, 1080),Refreshable {

    private val saveButton =  Button(
        width = 40, height = 40,
        posX = 150, posY = 50,

    ).apply {
        visual = CompoundVisual(
            ColorVisual(ColorEnum.Olivine.toRgbValue()),
            ImageVisual(path = "save.png")
        )
    }

    private val quitButton = Button(
        width = 40, height = 40,
        posX = 50, posY = 50,
    ).apply {
        visual = ImageVisual(path = "blackArrow.png")
    }

    private val undoButton = Button(
        width = 40, height = 40,
        posX = 250, posY = 50,
    ).apply {
        visual = CompoundVisual(
            ImageVisual(path = "undo.png"))
    }

    private val redoButton = Button(
        width = 40, height = 40,
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
        posX = 100, posY = 750, size =65, visual = ImageVisual(path = "longCurveTile.png"),
    ).apply { rotate(30) }

    private val routeStack = HexagonView(
        posX = 100, posY = 500, size =65, visual = CompoundVisual(
            ColorVisual(250,240,202),ImageVisual(path = "BacksideTile.png"))
    )

    //It is for layout of the playerName and score boxes
    private val playerLayout = Pane<ComponentView>(
        posX = 1500, posY = 50 , width = 400, height = 1000
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
            hexagonGrid, rotateLeftButton,rotateRightButton, playersTile, routeStack,playerLayout)
    }


     override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
         val playerNames = mutableListOf<Label>()
         for (i in players){
             val text = Label(text = i.name)
             playerNames.add(text)
         }

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