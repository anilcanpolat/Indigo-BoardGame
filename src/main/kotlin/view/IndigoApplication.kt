package view

import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.core.MenuScene
import view.ui.*
import java.awt.Menu

/**
 * Create scenes and give some buttons there functionality.
 * initialize the scenes afterwards
 */
class IndigoApplication : BoardGameApplication("Indigo-Game") {

    private val gameScene : GameScene = GameScene()

    private val welcomeScene : MenuScene = WelcomeScene().apply {
        loadGameButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(saveAndLoadScene)
        }

        hotSeatModeButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(chosePlayerCountScene)
        }

        quitButton.onMouseClicked = {
            exit()
        }
    }

    private val chosePlayerCountScene : MenuScene = ChosePlayerCountScene().apply {
       backButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)
       }

       p2Button.onMouseClicked = {
           this@IndigoApplication.showMenuScene(selectNameAndKiScene)
       }

       p3OwnButton.onMouseClicked = {
           this@IndigoApplication.showMenuScene(selectNameAndKiScene)
       }

       p3SharedButton.onMouseClicked = {
           this@IndigoApplication.showMenuScene(selectNameAndKiScene)
       }

       p4Button.onMouseClicked = {
           this@IndigoApplication.showMenuScene(selectNameAndKiScene)
       }
    }

    private val selectNameAndKiScene : MenuScene = SelectNameAndKiScene().apply {
        returnFromNameButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(chosePlayerCountScene)
        }
    }

    private val saveAndLoadScene : MenuScene = SaveAndLoadScene().apply {
        returnFromSaveButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)
        }
    }

    private val endGameScene : MenuScene = EndgameScene().apply {
        quitButton.onMouseClicked = {
            exit()
        }

        startGameButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(welcomeScene)
        }


    }

    init {
        this.showMenuScene(selectNameAndKiScene)
    }

}