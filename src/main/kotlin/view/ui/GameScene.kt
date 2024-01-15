package view.ui

import entity.Player
import entity.PlayerToken
import service.Refreshable
import tools.aqua.bgw.components.ComponentView
import tools.aqua.bgw.components.container.HexagonGrid
import tools.aqua.bgw.components.gamecomponentviews.HexagonView
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
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

    private val routeStack = HexagonView(
        posX = 100, posY = 500, size =65, visual = CompoundVisual(
            ColorVisual(250,240,202),ImageVisual(path = "BacksideTile.png"))
    ).apply { rotate(30) }

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



    //It is for layout of the playerName and score
    //Player 1 Box
    private val player1Pane = Pane<ComponentView>(
        width = 300, height =   200
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue()) }

    private val player1Text = Label(text = "Player1", posX = 10 ,
        font = Font(size = 30), width = 200,
        alignment = Alignment.CENTER_LEFT )
    private val player1Token = Label(visual = ImageVisual(path = "BigPlayerColorFour.png"),
        width = 70, height = 70, posX = 200, posY = 20
    )
    private val player1ScoreText = Label(text = "Score:", posX = 10, posY = 50, font = Font(size=30),
        width = 200, alignment = Alignment.CENTER_LEFT)
    private val player1Score = Label(text = "0",
        posX = 100, posY = 50, font = Font(size = 30), alignment = Alignment.CENTER_LEFT)
    private val player1Tile = HexagonView(
        posX = 10, posY = 70,
        visual = CompoundVisual(ColorVisual(250,240,202),ImageVisual(path = "BacksideTile.png")),
        size = 65).apply { rotate(30) }

    //Player 2 Box
    private val player2Pane = Pane<ComponentView>(
        width = 300, height = 200
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue()) }
    private val player2Text = Label(text = "Player2", posX = 10 ,
        font = Font(size = 30), width = 200,
        alignment = Alignment.CENTER_LEFT )
    private val player2Token = Label(visual = ImageVisual(path = "BigPlayerColorFour.png"),
        width = 70, height = 70, posX = 200, posY = 20
    )
    private val player2ScoreText = Label(text = "Score:", posX = 10, posY = 50, font = Font(size=30),
        width = 200, alignment = Alignment.CENTER_LEFT)
    private val player2Score = Label(text = "0",
        posX = 100, posY = 50, font = Font(size = 30), alignment = Alignment.CENTER_LEFT)
    private val player2Tile = HexagonView(
        posX = 10, posY = 70,
        visual = CompoundVisual(ColorVisual(250,240,202),ImageVisual(path = "BacksideTile.png")),
        size = 65).apply { rotate(30) }

    //Player3 Box
    private val player3Pane = Pane<ComponentView>(
        width = 300, height = 200
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue()) }
    private val player3Text = Label(text = "Player3", posX = 10 ,
        font = Font(size = 30), width = 200,
        alignment = Alignment.CENTER_LEFT )
    private val player3Token = Label(visual = ImageVisual(path = "BigPlayerColorFour.png"),
        width = 70, height = 70, posX = 200, posY = 20
    )
    private val player3ScoreText = Label(text = "Score:", posX = 10, posY = 50, font = Font(size=30),
        width = 200, alignment = Alignment.CENTER_LEFT)
    private val player3Score = Label(text = "0",
        posX = 100, posY = 50, font = Font(size = 30), alignment = Alignment.CENTER_LEFT)
    private val player3Tile = HexagonView(
        posX = 10, posY = 70,
        visual = CompoundVisual(ColorVisual(250,240,202),ImageVisual(path = "BacksideTile.png")),
        size = 65).apply { rotate(30) }

    //Player 4 Box
    private val player4Pane = Pane<ComponentView>(
       width = 300, height = 200
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue()) }
    private val player4Text = Label(text = "Player4", posX = 10 ,
        font = Font(size = 30), width = 200,
        alignment = Alignment.CENTER_LEFT )
    private val player4Token = Label(visual = ImageVisual(path = "BigPlayerColorFour.png"),
        width = 70, height = 70, posX = 200, posY = 20
    )
    private val player4ScoreText = Label(text = "Score:", posX = 10, posY = 50, font = Font(size=30),
        width = 200, alignment = Alignment.CENTER_LEFT)
    private val player4Score = Label(text = "0",
        posX = 100, posY = 50, font = Font(size = 30), alignment = Alignment.CENTER_LEFT)
    private val player4Tile = HexagonView(
        posX = 10, posY = 70,
        visual = CompoundVisual(ColorVisual(250,240,202),ImageVisual(path = "BacksideTile.png")),
        size = 65).apply { rotate(30) }

    private val gridPane = GridPane<ComponentView>(posX = 1750, posY = 500 , columns = 1, rows = 4, spacing = 20)

    //Lists of player labels
    private val playerNameList = listOf(player1Text, player2Text, player3Text, player4Text)
    private val playerTokenList = listOf(player1Token, player2Token, player3Token,player4Token)


    init {
        player1Pane.addAll(player1Text, player1Token, player1ScoreText, player1Score,player1Tile)
        player2Pane.addAll(player2Text, player2Token, player2ScoreText, player2Score,player2Tile)
        player3Pane.addAll(player3Text, player3Token, player3ScoreText, player3Score,player3Tile)
        player4Pane.addAll(player4Text, player4Token, player4ScoreText, player4Score,player4Tile)
        gridPane[0,0]= player1Pane
        gridPane[0,1] = player2Pane
        gridPane[0,2] = player3Pane
        gridPane[0,3] = player4Pane


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
        //For the images of TreasureTiles
        placeTiles()
        background = ColorVisual.LIGHT_GRAY
        addComponents(saveButton, quitButton,redoButton,undoButton,
            hexagonGrid, rotateLeftButton,rotateRightButton, playersTile, routeStack,gridPane)
    }


     override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
        when(players.size){
            2 -> for (i in 2..3){
                gridPane[0,i]!!.isDisabled = true
                gridPane[0,i]!!.isVisible = false
                }

            3-> {
                gridPane[0,3]!!.isDisabled = true
                gridPane[0,3]!!.isVisible = false
            }
        }
         for (i in 0..players.size){
             playerNameList[i].text= players[i].name
             playerTokenList[i].visual = ImageVisual(path = "PlayerColor"+ players[i].playerToken.toString() +".png")
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