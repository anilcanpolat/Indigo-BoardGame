package view.ui

import entity.PlayerConfig
import entity.PlayerType
import service.NetworkService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import view.IndigoApplication
import java.awt.Color

/**
 * this class is used to join a network game, it allows to input a game-id and
 * players name, then via join the players makes the call to join the game through the server
 */
class GuestButtonScene(networkService: NetworkService,
                       private val indigoApp: IndigoApplication) : MenuScene(1920, 1080,
    background = ImageVisual("cecihoney-background-desert-full.jpg")){

    val returnGuestButton = Button(
        posX = 10, posY = 10,
        width = 40, height = 40,
        visual = CompoundVisual(
            ImageVisual(
                path = "blackArrow.png")
        )
    )

    private val header = Label(
        posX = 780, posY = 150,
        width = 400, height = 150,
        text = "Join a networkgame: ",
        font = Font(40),
        visual = ColorVisual(ColorEnum.Olivine.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(15)
        }
    )

    private val playerATextBox = TextField(
        posX = 780, posY = 380,
        width = 250, height = 50,
        prompt = "Enter Name: ",
        text = "", font = Font(16)
    )

    private var kiLevelA = 0
    private var kiBoolean = false

    private val kiButtonA = Button(
        posX = 1040, posY = 380,
        width = 70, height = 50,
        text = "Easy", font = Font(16)
    ).apply { onMouseClicked = {
            if(kiLevelA == 0){
                kiLevelA = 1
                this.visual = ColorVisual(ColorEnum.Olivine.toRgbValue())
            } else{
                kiLevelA = 0
                this.visual = ColorVisual(Color.WHITE)
            }
            kiBoolean = !kiBoolean
        }
    }

    private val kiButtonB = Button(
        posX = 1120, posY = 380,
        width = 70, height = 50,
        text = "Hard", font = Font(16)
    ).apply { onMouseClicked = {
        if(kiLevelA == 0){
            kiLevelA = 1
            this.visual = ColorVisual(ColorEnum.EngOrange.toRgbValue())
        } else{
            kiLevelA = 0
            this.visual = ColorVisual(Color.WHITE)
        }
            kiBoolean = !kiBoolean
         }
    }

    private val kiSpeedGuestA = Label(
        posX = 790, posY = 440,
        width = 75, height = 50,
        text = "Ki-Speed: ", font = Font(16)
    )

    private val kiSpeedGuestB = Label(
        posX = 915, posY = 440,
        width = 50, height = 50,
        text = "ms ", font = Font(16)
    )

    private val kiSpeedGuestText = TextField(
        posX = 875, posY = 450,
        width = 40, height = 30,
        text = "250", font = Font(14)
    )

    val joinButton = Button(
        posX = 865, 600,
        250, 50,
        text = "Join ", font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Wheat.toRgbValue())
    onMouseClicked ={
        val player  = configureGuestPlayer()
        networkService.joinGame(guestIdField.text, player)
        indigoApp.showLobbyScene()
        }
    }

    private val guestIdLabel = Label(
        posX = 970, posY = 450,
        width = 40, height = 40,
        text = "Id: ", font = Font(16)
    ).apply {
        visual = ColorVisual(ColorEnum.Olivine.toRgbValue())
    }

    private val guestIdField = TextField(
        posX = 1020, 450,
        80, 40,
        prompt = "Enter id: ", font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Olivine.toRgbValue())
    }


    private fun configureGuestPlayer() : PlayerConfig{
        var isRandomKi = false
        var isPerson = PlayerType.PERSON
        if(kiLevelA == 2){
            isRandomKi = true
        }

        if(kiBoolean){
            isPerson = PlayerType.COMPUTER
        }

        val delay = try {
            Integer.parseInt(kiSpeedGuestText.text, 10)
        } catch (e: NumberFormatException) {
            println("Invalid speed value. Defaulting to 250ms!")
            250
        }

        return PlayerConfig(playerATextBox.text,0, isPerson, isRandomKi, delay)
    }

    init {
        addComponents(
            returnGuestButton, header,
            playerATextBox,
            kiButtonA, kiButtonB,
            kiSpeedGuestA, kiSpeedGuestB,
            kiSpeedGuestText,joinButton,
            guestIdField, guestIdLabel
        )
    }
}