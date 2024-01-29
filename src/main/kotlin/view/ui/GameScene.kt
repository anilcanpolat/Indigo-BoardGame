package view.ui

import entity.*
import service.Refreshable
import service.RootService
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
import tools.aqua.bgw.visual.SingleLayerVisual
import kotlin.math.absoluteValue

/**
 * show the game filed and all the user UI,
 * take inputs from the User and show results
 */
class GameScene(
    private val rootService: RootService,
    private val app: view.IndigoApplication
) : BoardGameScene(1920, 1080), Refreshable {


    val saveButton = Button(
        width = 40, height = 40,
        posX = 150, posY = 50, visual = CompoundVisual(
            ColorVisual(ColorEnum.Olivine.toRgbValue()),
            ImageVisual(path = "save.png")
        )
    )


    val quitButton = Button(
        width = 40, height = 40,
        posX = 50, posY = 50,
    ).apply {
        visual = ImageVisual(path = "blackArrow.png")
    }

    private val undoButton = Button(
        width = 40, height = 40,
        posX = 250, posY = 50, visual = CompoundVisual(ImageVisual(path = "undo.png"))
    ).apply { onMouseClicked = { rootService.undo() } }


    private val redoButton = Button(
        width = 40, height = 40,
        posX = 350, posY = 50, visual = CompoundVisual(
            ImageVisual(path = "redo.png")
        )
    ).apply {
        onMouseClicked = { rootService.redo() }
    }

    private val routeStack = HexagonView(
        posX = 100, posY = 500, size = 65, visual = CompoundVisual(
            ColorVisual(250, 240, 202), ImageVisual(path = "BacksideTile.png")
        )
    ).apply { rotate(30) }

    private val stackSize = Label(posX = 100, posY = 600, text = "")

    private var rotationRate: Int = 0

    private val rotateLeftButton = Button(
        width = 130, height = 50,
        posX = 100, posY = 900, visual = ColorVisual.GRAY, text = "Rotate Left"
    ).apply {
        onMouseClicked = {
            playersTile.apply { rotation -= 60 }
            rotationRate += 5
        }
    }

    private val rotateRightButton = Button(
        width = 130, height = 50,
        posX = 100, posY = 950, visual = ColorVisual.GRAY, text = "Rotate Right"
    ).apply {
        onMouseClicked = {
            playersTile.apply { rotation += 60 }
            rotationRate += 1
        }
    }

    private val hexagonGrid = HexagonGrid<HexagonView>(
        coordinateSystem = HexagonGrid.CoordinateSystem.AXIAL, posX = 900, posY = 450
    ).apply { rotate(30) }

    private val playersTile = HexagonView(
        posX = 100, posY = 750, size = 65, visual = ImageVisual(path = "longCurveTile.png"),
    ).apply { rotation = 90.0 }

    private val gate1Token1 = Label(
        width = 70, height = 70, posX = 1075, posY = 70, visual = ImageVisual(path = "PlayerColorCYAN.png")
    )
    private val gate1Token2 = Label(
        width = 70, height = 70, posX = 1175, posY = 120, visual = ImageVisual(path = "PlayerColorCYAN.png")
    )
    private val gate2Token1 = Label(
        width = 70, height = 70, posX = 1375, posY = 450, visual = ImageVisual(path = "PlayerColorCYAN.png")
    )
    private val gate2Token2 = Label(
        width = 70, height = 70, posX = 1375, posY = 550, visual = ImageVisual(path = "PlayerColorCYAN.png")
    )
    private val gate3Token1 = Label(
        width = 70, height = 70, posX = 1175, posY = 900, visual = ImageVisual(path = "PlayerColorCYAN.png")
    )
    private val gate3Token2 = Label(
        width = 70, height = 70, posX = 1075, posY = 950, visual = ImageVisual(path = "PlayerColorCYAN.png")
    )
    private val gate4Token1 = Label(
        width = 70, height = 70, posX = 700, posY = 950, visual = ImageVisual(path = "PlayerColorCYAN.png")
    )
    private val gate4Token2 = Label(
        width = 70, height = 70, posX = 600, posY = 900, visual = ImageVisual(path = "PlayerColorCYAN.png")
    )
    private val gate5Token1 = Label(
        width = 70, height = 70, posX = 400, posY = 550, visual = ImageVisual(path = "PlayerColorCYAN.png")
    )
    private val gate5Token2 = Label(
        width = 70, height = 70, posX = 400, posY = 450, visual = ImageVisual(path = "PlayerColorCYAN.png")
    )
    private val gate6Token1 = Label(
        width = 70, height = 70, posX = 600, posY = 120, visual = ImageVisual(path = "PlayerColorCYAN.png")
    )
    private val gate6Token2 = Label(
        width = 70, height = 70, posX = 700, posY = 70, visual = ImageVisual(path = "PlayerColorCYAN.png")
    )


    //It is for layout of the playerName and score
    //Player 1 Box
    private val player1Pane = Pane<ComponentView>(
        width = 300, height = 200
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue()) }

    private val player1Text = Label(
        text = "Player1", posX = 10,
        font = Font(size = 30), width = 200,
        alignment = Alignment.CENTER_LEFT
    )
    private val player1Token = Label(
        visual = ImageVisual(path = "BigPlayerColorFour.png"),
        width = 70, height = 70, posX = 200, posY = 20
    )
    private val player1ScoreText = Label(
        text = "Score:", posX = 10, posY = 50, font = Font(size = 30),
        width = 200, alignment = Alignment.CENTER_LEFT
    )
    private val player1Score = Label(
        text = "0",
        posX = 100, posY = 50, font = Font(size = 30), alignment = Alignment.CENTER_LEFT
    )
    private val player1Tile = HexagonView(
        posX = 10, posY = 70,
        visual = CompoundVisual(ColorVisual(250, 240, 202), ImageVisual(path = "BacksideTile.png")),
        size = 65
    ).apply { rotate(30) }

    //Player 2 Box
    private val player2Pane = Pane<ComponentView>(
        width = 300, height = 200
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue()) }
    private val player2Text = Label(
        text = "Player2", posX = 10,
        font = Font(size = 30), width = 200,
        alignment = Alignment.CENTER_LEFT
    )
    private val player2Token = Label(
        visual = ImageVisual(path = "BigPlayerColorFour.png"),
        width = 70, height = 70, posX = 200, posY = 20
    )
    private val player2ScoreText = Label(
        text = "Score:", posX = 10, posY = 50, font = Font(size = 30),
        width = 200, alignment = Alignment.CENTER_LEFT
    )
    private val player2Score = Label(
        text = "0",
        posX = 100, posY = 50, font = Font(size = 30), alignment = Alignment.CENTER_LEFT
    )
    private val player2Tile = HexagonView(
        posX = 10, posY = 70,
        visual = CompoundVisual(ColorVisual(250, 240, 202), ImageVisual(path = "BacksideTile.png")),
        size = 65
    ).apply { rotate(30) }

    //Player3 Box
    private val player3Pane = Pane<ComponentView>(
        width = 300, height = 200
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue()) }

    private val player3Text = Label(
        text = "Player3", posX = 10,
        font = Font(size = 30), width = 200,
        alignment = Alignment.CENTER_LEFT
    )
    private val player3Token = Label(
        visual = ImageVisual(path = "BigPlayerColorFour.png"),
        width = 70, height = 70, posX = 200, posY = 20
    )
    private val player3ScoreText = Label(
        text = "Score:", posX = 10, posY = 50, font = Font(size = 30),
        width = 200, alignment = Alignment.CENTER_LEFT
    )
    private val player3Score = Label(
        text = "0",
        posX = 100, posY = 50, font = Font(size = 30), alignment = Alignment.CENTER_LEFT
    )
    private val player3Tile = HexagonView(
        posX = 10, posY = 70,
        visual = CompoundVisual(ColorVisual(250, 240, 202), ImageVisual(path = "BacksideTile.png")),
        size = 65
    ).apply { rotate(30) }

    //Player 4 Box
    private val player4Pane = Pane<ComponentView>(
        width = 300, height = 200
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue()) }
    private val player4Text = Label(
        text = "Player4", posX = 10,
        font = Font(size = 30), width = 200,
        alignment = Alignment.CENTER_LEFT
    )
    private val player4Token = Label(
        visual = ImageVisual(path = "BigPlayerColorFour.png"),
        width = 70, height = 70, posX = 200, posY = 20
    )
    private val player4ScoreText = Label(
        text = "Score:", posX = 10, posY = 50, font = Font(size = 30),
        width = 200, alignment = Alignment.CENTER_LEFT
    )
    private val player4Score = Label(
        text = "0",
        posX = 100, posY = 50, font = Font(size = 30), alignment = Alignment.CENTER_LEFT
    )
    private val player4Tile = HexagonView(
        posX = 10, posY = 70,
        visual = CompoundVisual(ColorVisual(250, 240, 202), ImageVisual(path = "BacksideTile.png")),
        size = 65
    ).apply { rotate(30) }

    private val gridPane = GridPane<ComponentView>(posX = 1750, posY = 500, columns = 1, rows = 4, spacing = 20)

    //Lists of player labels
    private val playerNameList = listOf(player1Text, player2Text, player3Text, player4Text)
    private val playerTokenList = listOf(player1Token, player2Token, player3Token, player4Token)
    private val gatesTokenList = listOf(
        gate1Token1, gate1Token2, gate2Token1, gate2Token2, gate3Token1, gate3Token2,
        gate4Token1, gate4Token2, gate5Token1, gate5Token2, gate6Token1, gate6Token2
    )
    private val scoresList = listOf(player1Score, player2Score, player3Score, player4Score)


    init {
        player1Pane.addAll(player1Text, player1Token, player1ScoreText, player1Score, player1Tile)
        player2Pane.addAll(player2Text, player2Token, player2ScoreText, player2Score, player2Tile)
        player3Pane.addAll(player3Text, player3Token, player3ScoreText, player3Score, player3Tile)
        player4Pane.addAll(player4Text, player4Token, player4ScoreText, player4Score, player4Tile)
        gridPane[0, 0] = player1Pane
        gridPane[0, 1] = player2Pane
        gridPane[0, 2] = player3Pane
        gridPane[0, 3] = player4Pane

        //There should not be a coordinate like [4,1], [-4,-1] or [-4,-4] and others
        for (row in -4..4) {
            for (col in -4..4) {
                if ((row + col).absoluteValue >= 5) {
                    continue
                }

                val hexagon = HexagonView(visual = ColorVisual(250, 240, 202), size = 65).apply {
                    onMouseClicked = {
                        rootService.playerService.playerMove(
                            Pair(rootService.currentGame!!.currentPlayer.currentTile!!, rotationRate.mod(6)),
                            Pair(col, row)
                        )
                    }


                }
                hexagonGrid[col, row] = hexagon
            }
        }
        //For the images of TreasureTiles
        background = ColorVisual.LIGHT_GRAY


        addComponents(
            saveButton, quitButton, redoButton, undoButton,
            hexagonGrid, rotateLeftButton, rotateRightButton, playersTile, routeStack, stackSize, gridPane,
            gate1Token1, gate1Token2, gate2Token1, gate2Token2, gate3Token1, gate3Token2,
            gate4Token1, gate4Token2, gate5Token1, gate5Token2, gate6Token1, gate6Token2
        )
    }

    //Clone the list to reach the players after onGameStart
    private var playerList = listOf<Player>()


    override fun onGameStart(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>) {
        playerList = players
        changePlayerNamesAndGates(players,gates)
        highlightPlayer(rootService.currentGame!!.currentPlayer)
        playersTile.apply {
            visual = ImageVisual(
                findTilePath(rootService.currentGame!!.currentPlayer.currentTile!!.tileType)
            )
        }

        stackSize.apply { text = rootService.currentGame!!.drawPile.size.toString() }
        onStateChange(rootService.currentGame!!)
        placeTiles()
        val isPlayerRemote = players[0].playerType == PlayerType.REMOTE
        for (i in hexagonGrid.components) {

            if (isPlayerRemote) {
                i.apply { isDisabled = true }
            } else {
                i.apply { isDisabled = false }
            }
        }
        if (isPlayerRemote) {
            rotateLeftButton.apply { isDisabled = true }
            rotateRightButton.apply { isDisabled = true }
        } else {
            rotateLeftButton.apply { isDisabled = false }
            rotateRightButton.apply { isDisabled = false }
        }

        if (isNetworkGame()) {
            undoButton.apply {
                isDisabled = true
                isVisible = false
            }
            redoButton.apply {
                isDisabled = true
                isVisible = false
            }
            saveButton.apply {
                isDisabled = true
                isVisible = false
            }
        }

        app.hideMenuAndShowGame()
    }

    override fun onPlayerMove(player: Player, nextPlayer: Player, tile: Tile, position: Pair<Int, Int>, rotation: Int) {
        playersTile.apply { visual = ImageVisual(findTilePath(nextPlayer.currentTile!!.tileType)) }
        hexagonGrid[position.first, position.second]?.apply {
            visual = ImageVisual(findTilePath(tile.tileType))
            this.rotation = ((tile.rotation * 60) + 60).toDouble()
        }

        changeVisual(tile, position)

        highlightPlayer(nextPlayer)

        rotationRate = 0
        playersTile.apply { this.rotation = 90.0 }

        stackSize.apply { text = rootService.currentGame!!.drawPile.size.toString() }

        val isPlayerRemote = nextPlayer.playerType == PlayerType.REMOTE

        for (i in hexagonGrid.components) {

            if (isPlayerRemote) {
                i.apply { isDisabled = true }
            } else {
                i.apply { isDisabled = false }
            }
        }
        if (isPlayerRemote) {
            rotateLeftButton.apply { isDisabled = true }
            rotateRightButton.apply { isDisabled = true }
        } else {
            rotateLeftButton.apply { isDisabled = false }
            rotateRightButton.apply { isDisabled = false }
        }
    }

    override fun onGemMove(positionList: List<Pair<Pair<Int, Int>, Int>>) {
        val tile1 = rootService.currentGame!!.board.grid.grid[positionList.first().first]
        val tile2 = rootService.currentGame!!.board.grid.grid[positionList.last().first]


        changeVisual(tile1, positionList.first().first)
        changeVisual(tile2, positionList.last().first)


    }

    private fun changeVisual(tile: Tile?, hexCord: Pair<Int, Int>) {
        val visualList = mutableListOf<SingleLayerVisual>()
        for (i in 0 until tile!!.gems.size) {
            if (tile.gems[i] == null) {
                visualList.add(ColorVisual.TRANSPARENT)
            } else {
                if (hexCord == Pair(0,-4) || hexCord == Pair(0,4) || hexCord == Pair(-4,4) || hexCord == Pair(-4,0)
                    || hexCord == Pair(4,0) || hexCord == Pair(4,-4)){
                    visualList.add(ImageVisual(findGemPath(tile.gems[i], i, tile.rotation-1)))
                }
                else{
                visualList.add(ImageVisual(findGemPath(tile.gems[i], i, tile.rotation)))}
            }
        }

        val compoundVisual = CompoundVisual(
            ImageVisual(findTilePath(tile.tileType)),
            visualList[0], visualList[1], visualList[2], visualList[3], visualList[4], visualList[5], visualList[5]
        )

        if (hexCord == Pair(0, -4) || hexCord == Pair(0, 4) || hexCord == Pair(-4, 4) || hexCord == Pair(-4, 0)
            || hexCord == Pair(4, 0) || hexCord == Pair(4, -4)
        ) {
            hexagonGrid[hexCord.first, hexCord.second]!!.apply {
                visual = compoundVisual
            }
        } else {
            hexagonGrid[hexCord.first, hexCord.second]!!.apply {
                visual = compoundVisual
                this.rotation = ((tile.rotation * 60) + 60).toDouble()
            }
        }

    }


    private fun findGemPath(gem: Gem?, edge: Int, rotation: Int): String {
        val edgeVal = (edge - rotation).mod(6)
        when (gem) {
            Gem.AMBER -> {
                return when (edgeVal) {
                    0 -> "yellowGem0.png"
                    1 -> "yellowGem1.png"
                    2 -> "yellowGem2.png"
                    3 -> "yellowGem3.png"
                    4 -> "yellowGem4.png"
                    5 -> "yellowGem5.png"
                    else -> "yellowGem0.png"
                }
            }

            Gem.EMERALD -> {
                return when (edgeVal) {
                    0 -> "greenGem0.png"
                    1 -> "greenGem1.png"
                    2 -> "greenGem2.png"
                    3 -> "greenGem3.png"
                    4 -> "greenGem4.png"
                    5 -> "greenGem5.png"
                    else -> "greenGem0.png"
                }
            }

            Gem.SAPHIRE -> {
                return when (edgeVal) {
                    0 -> "blueGem0.png"
                    1 -> "blueGem1.png"
                    2 -> "blueGem2.png"
                    3 -> "blueGem3.png"
                    4 -> "blueGem4.png"
                    5 -> "blueGem5.png"
                    else -> "blueGem0.png"
                }
            }

            null -> return "redo.png"
        }
    }

    override fun onGemRemoved(fromTile: Pair<Int, Int>, edge: Int) {
        for (i in rootService.currentGame!!.players.indices) {
            scoresList[i].text = calcScore(rootService.currentGame!!.players[i].collectedGems).toString()
        }
        changeVisual(rootService.currentGame!!.board.grid.grid[fromTile], fromTile)
    }

    override fun onStateChange(newGameState: GameState) {
        rotationRate = 0

        playerList = newGameState.players
        changePlayerNamesAndGates(playerList, newGameState.board.gates.toList())
        playersTile.apply {
            visual = ImageVisual(
                findTilePath(newGameState.currentPlayer.currentTile!!.tileType)
            )
            this.rotation = 90.0
        }
        for (i in hexagonGrid.components) {
            i.apply { visual = ColorVisual(250, 240, 202) }
        }
        placeTiles()
        for ((key, value) in newGameState.board.grid.grid) {
            changeVisual(value, key)
        }

        for (i in playerList.indices) {
            scoresList[i].text = calcScore(playerList[i].collectedGems).toString()
        }
        stackSize.apply { text = newGameState.drawPile.size.toString() }



    }


    private fun placeTiles() {
        //up
        hexagonGrid[0, -4]?.apply {
            visual = CompoundVisual(
                ImageVisual(path = "TreasureTileOutside.png"),
                ImageVisual(path = "yellowGem0.png"),
            ).apply { rotation=240.0 }
        }
        //down
        hexagonGrid[0, 4]?.apply {
            visual = CompoundVisual(
                ImageVisual(path = "TreasureTileOutside.png"),
                ImageVisual(path = "yellowGem0.png"),
            ).apply { rotation = 60.0 }
        }
        //down left
        hexagonGrid[-4, 4]!!.apply {
            visual = CompoundVisual(
                ImageVisual(path = "TreasureTileOutside.png"),
                ImageVisual(path = "yellowGem0.png"),
            ).apply { rotation = 120.0 }
        }
        //up left
        hexagonGrid[-4, 0]!!.apply {
            visual = CompoundVisual(
                ImageVisual(path = "TreasureTileOutside.png"),
                ImageVisual(path = "yellowGem0.png"),
            ).apply { rotation = 180.0 }
        }
        //down right
        hexagonGrid[4, 0]!!.apply {
            visual = CompoundVisual(
                ImageVisual(path = "TreasureTileOutside.png"),
                ImageVisual(path = "yellowGem0.png"),
            )
        }
        //up right
        hexagonGrid[4, -4]!!.apply {
            visual = CompoundVisual(
                ImageVisual(path = "TreasureTileOutside.png"),
                ImageVisual(path = "yellowGem0.png"),
            ).apply { rotation = -60.0 }
        }


    }

    //To add highlight to currentPlayer and delete other highlights
    private fun highlightPlayer(player: Player) {
        for (i in playerNameList) {
            if (i.text == player.name) {
                i.apply { visual = ColorVisual(ColorEnum.Olivine.toRgbValue()) }
            } else i.apply { visual = ColorVisual.TRANSPARENT }
        }
    }


    private fun findTilePath(type: TileType): String {
        return when (type) {
            TileType.LONG_CURVES -> "StraightAndLongCurveTile.png"
            TileType.STRAIGHTS_ONLY -> "XTile.png"
            TileType.STRAIGHT_NOCROSS -> "StraightAndCurveTile.png"
            TileType.CURVES_TO_CORNER -> "longCurveTile.png"
            TileType.CORNERS_ONLY -> "CurveTile.png"
            TileType.TREASURE_CORNER -> "TreasureTileOutside.png"
            TileType.TREASURE_CENTER -> "TreasureTileInside.png"
        }
    }

    private fun calcScore(gems: MutableList<Gem>): Int {
        var score = 0
        for (i in gems) {
            score += i.score()
        }
        return score
    }

    private fun changePlayerNamesAndGates(players: List<Player>, gates: List<Pair<PlayerToken, PlayerToken>>){
        gridPane[0, 0]!!.isDisabled = false
        gridPane[0, 0]!!.isVisible = true
        gridPane[0, 1]!!.isDisabled = false
        gridPane[0, 1]!!.isVisible = true
        gridPane[0, 2]!!.isDisabled = false
        gridPane[0, 2]!!.isVisible = true
        gridPane[0, 3]!!.isDisabled = false
        gridPane[0, 3]!!.isVisible = true
        when (players.size) {
            2 -> for (i in 2..3) {
                gridPane[0, i]!!.isDisabled = true
                gridPane[0, i]!!.isVisible = false
            }

            3 -> {
                gridPane[0, 3]!!.isDisabled = true
                gridPane[0, 3]!!.isVisible = false
            }
        }
        for (i in players.indices) {
            playerNameList[i].text = players[i].name
            playerTokenList[i].visual = ImageVisual(path = "PlayerColor" + players[i].playerToken.toString() + ".png")

        }

        var index = 0
        for (i in gates.indices) {
            gatesTokenList[index++].visual = ImageVisual(path = "PlayerColor" + gates[i].first.toString() + ".png")
            gatesTokenList[index++].visual = ImageVisual(path = "PlayerColor" + gates[i].second.toString() + ".png")
        }
    }

    private fun isNetworkGame() =
        checkNotNull(rootService.currentGame).players.any {
            it.playerType == PlayerType.REMOTE
        }
}