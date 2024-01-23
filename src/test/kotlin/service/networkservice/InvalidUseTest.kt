package service.networkservice

import org.junit.jupiter.api.assertThrows
import kotlin.test.*
import service.*

/** test cases for [NetworkService] making sure that methods fail in certain situations */
class InvalidUseTest {
    /** make sure [NetworkService.sendTilePlaced] fails when no game has been created */
    @Test
    fun preventUninitialisedUse() {
        val net = NetworkService(RootService())

        assertThrows<IllegalStateException> {
            net.sendTilePlaced(0, Pair(1, -1))
        }
    }

    /** make sure [NetworkService.sendTilePlaced] won't accept invalid positions or rotations */
    @Test
    fun preventSendingInvalidMessages() {
        val net = NetworkService(RootService())

        assertThrows<IllegalArgumentException> {
            net.sendTilePlaced(0, Pair(4, 4))
        }

        assertThrows<IllegalArgumentException> {
            net.sendTilePlaced(-3, Pair(0, 1))
        }

        assertThrows<IllegalArgumentException> {
            net.sendTilePlaced(-3, Pair(4, 4))
        }
    }

    /**
     * make sure [NetworkService.sendTilePlaced] cannot send tile
     * placement messages with positions used by treasure tiles
     * */
    @Test
    fun preventSendingReservedPositions() {
        val net = NetworkService(RootService())

        assertThrows<IllegalArgumentException> {
            net.sendTilePlaced(0, Pair(4, 0))
        }

        assertThrows<IllegalArgumentException> {
            net.sendTilePlaced(0, Pair(4, -4))
        }
    }
}