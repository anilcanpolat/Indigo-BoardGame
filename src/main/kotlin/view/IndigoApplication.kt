package view

import tools.aqua.bgw.core.BoardGameApplication
import view.ui.*

/**
 * Create scenes and give some buttons there functionality.
 * initialize the scenes afterwards
 */
class IndigoApplication : BoardGameApplication("Indigo-Game") {
    private val gameScene = GameScene()

    private val welcomeScene = WelcomeScene().apply {
        quitButton.onMouseClicked = {
            exit()
        }
    }

    private val chosePlayerCountScene = ChosePlayerCountScene().apply {
   //     backButton.onMouseClicked = {
   //         this@IndigoApplication.showMenuScene(SelectNameAndKiScene)
   //     }
    }

    private val selectNameAndKiScene = SelectNameAndKiScene()

    private val saveAndLoadScene = SaveAndLoadScene()

    private val endGameScene = EndgameScene()

    init {
        this.showMenuScene(selectNameAndKiScene)
    }

}