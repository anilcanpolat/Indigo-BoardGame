package entity

/**
 * wrapper around values required to construct a player
 * @property useRandomAI when [type] is [PlayerType.COMPUTER] use random moves instead of proper calculated ones
 * @property aiDelay amount of time to wait until the move is actually executed
 */
data class PlayerConfig(
    val name: String,
    val age: Int,
    val type: PlayerType,
    val useRandomAI: Boolean = false,
    val aiDelay: Int = 0
)