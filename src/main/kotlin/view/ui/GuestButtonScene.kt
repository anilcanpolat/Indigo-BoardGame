package view.ui

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual

class GuestButtonScene : MenuScene(1920, 1080,
    background = ImageVisual("cecihoney-background-desert-full.jpg")){

    val returnFromNameButton = Button(
        posX = 10, posY = 10,
        width = 40, height = 40,
        visual = CompoundVisual(
            ImageVisual(
                path = "blackArrow.png")
        )
    )

    private val playerATextBox = TextField(
        posX = 760, posY = 200,
        width = 250, height = 50,
        prompt = "Put in Name! "
    )

    private val kiButtonA = Button(
        posX = 1035, posY = 200,
        width = 60, height = 50,
        text = "Add Ki"
    )

    private val kiButtonB = Button(
        posX = 1100, posY = 200,
        width = 60, height = 50,
        text = "Add Ki"
    )

    private var kiLevelA = 0

    private fun setKiLevel(){
        if(kiLevelA == 0){
            println("stuff")
            kiLevelA = 1
        }
    }

    init {
        addComponents(
            returnFromNameButton,
            playerATextBox,
            kiButtonA, kiButtonB
        )
    }
}