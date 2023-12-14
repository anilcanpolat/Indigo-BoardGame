package service


import edu.udo.cs.sopra.ntf.GameMode
import entity.Player

/**
 * Main class of the service layer for the card game. Provides access
 * to all other service classes and holds the [currentGame] state for these
 * services to access.
 */
class RootService {
 /**
  * Startet ein neues Spiel mit den angegebenen Spielern.
  *
  * @param playerList Die Liste der Spieler, die am Spiel teilnehmen.
  */
 fun startNewGame(playerList: List<Player>,gameMode:GameMode) {

 }
}