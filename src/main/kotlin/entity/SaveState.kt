package entity

import kotlinx.serialization.Serializable

@Serializable
data class SaveState(val states: List<SerializableGameState>, val currentState: Int)