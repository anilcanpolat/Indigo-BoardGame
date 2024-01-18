package service.networkservice

import entity.PlayerConfig
import entity.PlayerType

/**
 * Global constants used within the test cases for [service.NetworkService].
 * @property TEST_TIMEOUT Amount of time in milliseconds that may pass before a test step fails.
 * @property ALICE Config to create a test player named "Alice".
 * @property BOB Config to create a test player named "Bob".
 */
object NetworkConfig {
    const val TEST_TIMEOUT: Long = 1000

    val ALICE: PlayerConfig = PlayerConfig("Alice", -1, PlayerType.PERSON)
    val BOB: PlayerConfig = PlayerConfig("Bob", -1, PlayerType.PERSON)
    val CHARLIE: PlayerConfig = PlayerConfig("Charlie", -1, PlayerType.PERSON)
    val DAVE: PlayerConfig = PlayerConfig("Dave", -1, PlayerType.PERSON)
}
