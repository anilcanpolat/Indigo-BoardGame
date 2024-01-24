package view.ui

import entity.PlayerConfig
import service.Refreshable
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.BorderColor
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.style.BorderWidth
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color


/**
 * creates a wait menu which shows who and how many players
 * have already joined our game, when we host a network game.
 */
class LobbyScene : MenuScene(1920, 1080,
    background = ImageVisual("cecihoney-background-desert-full.jpg")), Refreshable {

    val backFromLobbyScene = Button(width = 40, height = 40,
        posX = 1, posY = 1, visual = CompoundVisual(
            ImageVisual(
                path = "blackArrow.png"
            )
        )
    )

    private val headLabel = Label(
        width = 350, height = 100,
        posX = 810, posY = 105,
        text = "Lobby",
        font = Font(size = 40)
    )

    var lobbyId = Label(
        width = 200, height = 100,
        posX = 910, posY = 525,
        text = "LobbyId",
        font = Font(40)
    ).apply { visual = ColorVisual(ColorEnum.Wheat.toRgbValue()) }

    //Labels for names and to show how many players are left to still join.
    val hostNameLabel = Label(
        posX = 355, posY = 145,
        width = 250, height = 250,
        font = Font(20)
    ).apply { visual = ColorVisual(Color.WHITE) }

    private val p2Label = Label(
        posX = 1315, posY = 145,
        width = 250, height = 250,
        font = Font(20)
    ).apply { visual = ColorVisual(Color.WHITE) }

    private val p3Label = Label(
        posX = 355, posY = 685,
        width = 250, height = 250,
        font = Font(20),
        visual = ColorVisual(Color.WHITE).apply {
            borderRadius = BorderRadius.XL
            borderColor = BorderColor.BLACK
            borderWidth = BorderWidth.LARGE
        }
    )

    private val p4Label = Label(
        posX = 1315, posY = 685,
        width = 250, height = 250,
        font = Font(20),
        visual = ColorVisual(Color.WHITE).apply {
            borderRadius = BorderRadius.XL
            borderColor = BorderColor.BLACK
            borderWidth = BorderWidth.LARGE
        }
    )


    /**
     * small help function to disable labels not needed when loading the game with
     * less than 4 players.
     */
    fun disableLabels(amountOfPlayers: Int){
        when(amountOfPlayers) {
            2 -> {
                p3Label.isVisible = false
                p4Label.isVisible = false
            }
            3 -> {
                p4Label.isVisible = false
            }
        }
    }

    override fun onPlayerJoinedGame(playerConfig: PlayerConfig) {
        if(p2Label.text == ""){
            p2Label.text = playerConfig.name
        }else if(p3Label.text == ""){
            p3Label.text = playerConfig.name
        }else if (p4Label.text == "") {
            p4Label.text = playerConfig.name
        }
    }


    init {
        opacity = 0.5
        addComponents(headLabel, backFromLobbyScene, lobbyId,
            hostNameLabel, p2Label, p3Label, p4Label)
    }
}