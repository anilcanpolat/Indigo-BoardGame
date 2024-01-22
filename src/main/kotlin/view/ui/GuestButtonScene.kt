package view.ui

import entity.Player
import entity.PlayerConfig
import entity.PlayerType
import service.NetworkService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

class GuestButtonScene(networkService: NetworkService) : MenuScene(1920, 1080,
    background = ImageVisual("cecihoney-background-desert-full.jpg")){

    val returnGuestButton = Button(
        posX = 10, posY = 10,
        width = 40, height = 40,
        visual = CompoundVisual(
            ImageVisual(
                path = "blackArrow.png")
        )
    )

    private val playerATextBox = TextField(
        posX = 760, posY = 500,
        width = 250, height = 50,
        text = "Enter Name: "
    )

    private var kiLevelA = 0
    private var kiBoolean = false

    private val kiButtonA = Button(
        posX = 1030, posY = 500,
        width = 60, height = 50,
        text = "Add Ki"
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
        posX = 1100, posY = 500,
        width = 60, height = 50,
        text = "Add Ki"
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
        posX = 770, posY = 650,
        width = 75, height = 50,
        text = "Ki-Speed: "
    )

    private val kiSpeedGuestB = Label(
        posX = 895, posY = 650,
        width = 50, height = 50,
        text = "ms "
    )

    private val kiSpeedGuestText = TextField(
        posX = 855, posY = 650,
        width = 40, height = 30,
        text = "250"
    )

    val joinButton = Button(
        posX = 860, 700,
        200, 50,
        text = "Join "
    ).apply { visual = ColorVisual(ColorEnum.Wheat.toRgbValue())
    onMouseClicked ={
        val player  = configureGuestPlayer()
        networkService.joinGame(guestIdField.text, player)

    }}

    private val guestIdField = TextField(
        posX = 885, 600,
        150, 30,
        prompt = "Enter id: "
    ).apply { visual = ColorVisual(ColorEnum.Wheat.toRgbValue())
        onKeyTyped = {
        }
    }


    private fun configureGuestPlayer() : PlayerConfig{
        return PlayerConfig(playerATextBox.text,0,PlayerType.REMOTE)
    }

    init {
        addComponents(
            returnGuestButton,
            playerATextBox,
            kiButtonA, kiButtonB,
            kiSpeedGuestA, kiSpeedGuestB,
            kiSpeedGuestText,joinButton, guestIdField
        )
    }
}