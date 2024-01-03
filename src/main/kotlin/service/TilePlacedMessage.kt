package service

import tools.aqua.bgw.net.common.GameAction
import tools.aqua.bgw.net.common.annotations.GameActionClass

/**
 * Workaround for the broken [edu.udo.cs.sopra.ntf.TilePlacedMessage] class.
 * Incorrect attribute names prevent receiving valid tile placement messages.
 * Once the ntf variant is fixed, this class can be removed.
 */
@GameActionClass
class TilePlacedMessage(
    val rotation: Int,
    val qcoordinate: Int,
    val rcoordinate: Int
): GameAction() {
    init {
        require(rotation in 0..5)
        require(qcoordinate in -4..4)
        require(rcoordinate in -4..4)
    }
}