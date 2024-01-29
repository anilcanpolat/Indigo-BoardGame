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
    val collectedGems: MutableList<Gem> = mutableListOf(),
    val useRandomAI: Boolean = false,
    val aiDelay: Int = 0
) {
    /** create a deepCopy of the current [Player] instance */
    fun deepCopy(): Player =
        Player(name, age, playerType, playerToken,
            currentTile?.deepCopy(), collectedGems.toMutableList(),
            useRandomAI, aiDelay)
}