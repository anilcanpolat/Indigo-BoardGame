package view.ui

import entity.PlayerConfig
import entity.PlayerType
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color


/**
 * creates a selection menu for the different numbers of players
 */
class HostGameScene : MenuScene(1920, 1080,
    background = ImageVisual("cecihoney-background-desert-full.jpg")) {

    private val headLabel = Label(
        width = 350, height = 75,
        posX = 810, posY = 105,
        text = "Put in your name and chose a gamemode",
        font = Font(size = 18)
    )

    val hostNameTextfield = TextField(
        posX = 835, posY = 175,
        width = 250, height = 50
    )

    private var kiLevelA = 0
    private var kiBoolean = false

    private val kiButtonA = Button(
        posX = 1100, posY = 175,
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
        posX = 1170, posY = 175,
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

    val host2Pl = Button(
        width = 200, height = 50,
        posX = 860, posY = 400,
        text = "2 Player"
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue()) }

    val host3PlShared = Button(
        width = 200, height = 50,
        posX = 860, posY = 500,
        text = "3 Player (Shared Gates)"
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue())}

    val host3Pl = Button(
        width = 200, height = 50,
        posX = 860, posY = 600,
        text = "3 Player (Own Gates)"
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue())}

    val host4Pl = Button(
        width = 200, height = 50,
        posX = 860, posY = 700,
        text = "4 Player"
    ).apply { visual = ColorVisual(ColorEnum.Papaya.toRgbValue())}

    val backFromHostGameScene = Button(width = 40, height = 40,
        posX = 1, posY = 1, visual = CompoundVisual(
            ImageVisual(
                path = "blackArrow.png"
            )
        )
    )

    private fun calcKiLevel(): Boolean{
        return kiLevelA == 1
    }

    /**
     * comment your shit please.
     */
    fun remoteConfigList(playerCount: Int): MutableList<PlayerConfig>{
        val typeList: MutableList<PlayerConfig> = mutableListOf()

        val p1Name = hostNameTextfield.text
        var p1Type: PlayerType = PlayerType.PERSON
        if(kiBoolean){
            p1Type = PlayerType.COMPUTER
        }

        val p1 = PlayerConfig(p1Name, 0, p1Type, calcKiLevel(), 200)

        typeList.add(p1)
        return typeList
    }

    init {
        opacity = 0.5
        addComponents(host2Pl, host3PlShared, host3Pl, host4Pl,
            kiButtonA, kiButtonB,
            hostNameTextfield, headLabel, backFromHostGameScene)
    }
}