package entity

import kotlinx.serialization.Serializable

/**
 * Data class used while serializing and deserializing save states.
 * Used by [service.RootService.save] and [service.RootService.load].
 * @property states A list of all states reachable using the [GameState.previousState]
 *                  and [GameState.nextState] references in chronological order.
 * @property currentState Index the [service.RootService.currentGame] is stored at.
 */
@Serializable
data class SaveState(val states: List<SerializableGameState>, val currentState: Int)