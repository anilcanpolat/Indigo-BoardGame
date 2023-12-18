package service

import entity.*

/**
 * This interface provides a mechanism for the service layer classes to communicate
 * (usually to the view classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * Default (empty) implementations are provided for all methods, so that implementing
 * UI classes only need to react to events relevant to them.
 *
 * @see AbstractRefreshingService
 *
 */
interface Refreshable {


    /**
     * perform refreshes that are necessary after a new game started
     */
    fun onGameStart(players :List<Player>,gates:List<Pair<PlayerToken,PlayerToken>>) {}


    /**
     * perform refreshes after a player has done an action
     */
    fun onPlayerMove(player: Player,nextPlayer: Player,tile: Tile,position:Pair<Int,Int>,rotation:Int) {}

    /**
     * perform refreshes after gem is moved
     */
    fun onGemMove (positionList:List<Pair<Pair<Int,Int>,Int>>,edge:Int) {}

    /**
     * perform refreshes after GameState has changed
     */
    fun onStateChange(newGameState: GameState) {

    }

    /**
     * expect a new input from a hotseat player
     */
    fun onWaitForInput(){

    }

    /**
     * perform refreshes after a gem is  removed
     */
    fun onGemRemoved(fromTile:Pair<Int,Int>,edge:Int){}
    /**
     * perform refreshes after the game has ended
     *
     */
    fun onGameFinished(players: List<Player>) {}


}
