package view

import tools.aqua.bgw.core.BoardGameApplication

class SopraApplication : BoardGameApplication("SoPra Game") {

   private val helloScene = HelloScene()

   init {
        this.showGameScene(helloScene)
    }

}

