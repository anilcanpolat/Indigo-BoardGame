package view

import tools.aqua.bgw.core.BoardGameApplication
import view.ui.*

class IndigoApplication : BoardGameApplication("Indigo-Game") {
    private val gameScene = GameScene()

    private val welcomeScene = WelcomeScene().apply {
        quitButton.onMouseClicked = {
            exit()
        }
    }

    private val chosePlayerCountScene = ChosePlayerCountScene()

    private val selectNameAndKiScene = SelectNameAndKiScene()

    private val saveAndLoadScene = SaveAndLoadScene()

    private val endGameScene = EndgameScene()

    init {
        this.showMenuScene(selectNameAndKiScene)
    }

}