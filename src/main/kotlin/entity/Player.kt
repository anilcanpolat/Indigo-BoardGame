package entity
import kotlinx.serialization.Serializable

@Serializable
/**
 * Class wrapping the state associated with a single player.
 */

data class Player(
    val name: String,
    val age: Int,
    val playerType: PlayerType,
    val playerToken: PlayerToken,
    var currentTile: Tile? = null,
    val collectedGems: MutableList<Gem> = mutableListOf()
)
