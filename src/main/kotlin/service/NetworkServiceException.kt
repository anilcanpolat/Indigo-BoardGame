package service

/**
 * Exception thrown when [NetworkService.createGame], [NetworkService.joinGame] or
 * [NetworkService.sendTilePlaced] fail in some way.
 */
class NetworkServiceException(private val type: Type): RuntimeException() {
    /** type of the network error */
    enum class Type {
        CannotConnectToServer,
        CannotCreateGame,
        CannotJoinGame,
    }

    override val message: String
        get() = when (type) {
            Type.CannotConnectToServer -> "cannot connect to the bgw server"
            Type.CannotJoinGame -> "cannot join the specified bgw game"
            Type.CannotCreateGame -> "cannot create a new bgw game"
        }
}
