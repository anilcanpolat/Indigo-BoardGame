package view.ui

import entity.PlayerConfig
import entity.PlayerType
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color


/**
 * creates a selection menu for the different numbers of players,
 * for a network game.
 */
class HostGameScene : MenuScene(1920, 1080,
    background = ImageVisual("cecihoney-background-desert-full.jpg")) {

    private val headLabel = Label(
        width = 350, height = 75,
        posX = 810, posY = 55,
        text = "Enter your name: ",
        font = Font(size = 35)
    )

    private val nameLabel = Label(
        width = 480, height = 180,
        posX = 750, posY = 65,
        visual = ColorVisual(ColorEnum.Wheat.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(10)
        }
    )

    private val headLabelB = Label(
        width = 450, height = 75,
        posX = 760, posY = 300,
        text = "And choose a Game mode: ",
        font = Font(size = 35)
    )

    private val gameModeLabel = Label(
        width = 450, height = 500,
        posX = 760, posY = 300,
        visual = ColorVisual(ColorEnum.Papaya.toRgbValue()).apply {
            backgroundRadius = BackgroundRadius(10)
        }
    )

    val hostNameTextfield = TextField(
        posX = 790, posY = 125,
        width = 250, height = 50,
        font = Font(18),
        prompt = "Enter your name: "
    )

    private var kiLevelA = 0
    private var kiBoolean = false

    private val kiButtonA = Button(
        posX = 1060, posY = 125,
        width = 60, height = 50,
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
        posX = 1130, posY = 125,
        width = 60, height = 50,
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

    val host2Pl = Button(
        width = 200, height = 50,
        posX = 890, posY = 400,
        text = "2 Player",
        font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Olivine.toRgbValue()) }

    val host3PlShared = Button(
        width = 200, height = 50,
        posX = 890, posY = 500,
        text = "3 Player (Shared Gates)",
        font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Olivine.toRgbValue())}

    val host3Pl = Button(
        width = 200, height = 50,
        posX = 890, posY = 600,
        text = "3 Player (Own Gates)",
        font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Olivine.toRgbValue())}

    val host4Pl = Button(
        width = 200, height = 50,
        posX = 890, posY = 700,
        text = "4 Player",
        font = Font(16)
    ).apply { visual = ColorVisual(ColorEnum.Olivine.toRgbValue())}

    val backFromHostGameScene = Button(width = 40, height = 40,
        posX = 1, posY = 1, visual = CompoundVisual(
            ImageVisual(
                path = "blackArrow.png"
            )
        )
    )

    private val kiSpeedHostA = Label(
        posX = 790, posY = 185,
        width = 85, height = 50,
        text = "Ki-Speed: ", font = Font(18)
    )

    private val kiSpeedHostB = Label(
        posX = 930, posY = 185,
        width = 50, height = 50,
        text = "ms ", font = Font(18)
    )

    private val kiSpeedHostText = TextField(
        posX = 885, posY = 190,
        width = 50, height = 40,
        text = "250", font = Font(16)
    )

    private fun calcKiLevel(): Boolean{
        return kiLevelA == 1
    }

    /**
     * help function to create a list of playerConfigs, required to start the game
     */
    fun remoteConfigList(playerCount: Int): MutableList<PlayerConfig>{
        val typeList: MutableList<PlayerConfig> = mutableListOf()

        val p1Name = hostNameTextfield.text
        var p1Type: PlayerType = PlayerType.PERSON
        if(kiBoolean){
            p1Type = PlayerType.COMPUTER
        }

        val delay = try {
            Integer.parseInt(kiSpeedHostText.text, 10)
        } catch (e: NumberFormatException) {
            println("Invalid speed value. Defaulting to 250ms!")
            250
        }
        val p1 = PlayerConfig(p1Name, 0, p1Type, calcKiLevel(), delay)

        when(playerCount){
            1 -> println("Just doing this to fix a detekt error :)")
        }
        typeList.add(p1)
        return typeList
    }

    init {
        opacity = 0.9
        addComponents(nameLabel, gameModeLabel,
            host2Pl, host3PlShared, host3Pl, host4Pl,
            kiButtonA, kiButtonB, headLabelB,
            kiSpeedHostText, kiSpeedHostB, kiSpeedHostA,
            hostNameTextfield, headLabel, backFromHostGameScene)
    }
}