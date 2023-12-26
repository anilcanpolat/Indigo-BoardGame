package entity

/**
 * Enumeration over the kinds of players present in the game.
 * The type of a player will be used by the service layer
 * to check where inputs have to come from.
 */
enum class PlayerType {
    PERSON,
    COMPUTER,
    REMOTE,
}
