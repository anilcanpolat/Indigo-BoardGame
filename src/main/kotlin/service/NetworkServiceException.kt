package service

class NetworkServiceException(private val type: Type): RuntimeException() {
    enum class Type {
        CannotConnectToServer,
        CannotJoinGame,
    }

    override val message: String
        get() = when (type) {
            Type.CannotConnectToServer -> "cannot connect to the bgw server"
            Type.CannotJoinGame -> "cannot join the specified bgw game"
        }
}
