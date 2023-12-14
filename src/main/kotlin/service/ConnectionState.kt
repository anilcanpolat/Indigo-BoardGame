package service

/**
 * States the [[NetworkService]] state machine can be in.
 */
enum class ConnectionState {
    DISCONNECTED,
    CONNECTED,
    HOST_WAITING_FOR_CONFIRMATION,
    GUEST_WAITING_FOR_CONFIRMATION,
    WAITING_FOR_GUESTS,
    WAITING_FOR_INIT,
    PLAYING_TURN,
    WAITING_FOR_OPPONENT
}
