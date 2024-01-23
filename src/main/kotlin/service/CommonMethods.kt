package service

import kotlin.math.max
import kotlin.math.absoluteValue

import entity.GameState
import entity.Tile
import entity.TileType
import java.util.HashSet
import kotlin.random.Random

/**
 * Some utility functions that are required in multiple spots throughout the program.
 */
object CommonMethods {
    /**
     * Check whether the current player is allowed to execute a given move.
     * A move is valid if and only if
     * - the current player is the owner of the passed tile
     * - the position on the board is empty
     * - the position on the board is valid
     * - the tile placement will not block any gates
     * - the tile passed is not a treasure tile
     * @param state [GameState] instance in which the move should be executed.
     * @param tile [Tile] object to place on the board.
     * @param rotation The amount of steps the tile should be rotated before placement.
     * @param position The position on the board the tile should be placed on.
     */
    fun isValidMove(state: GameState, tile: Tile, rotation: Int, position: Pair<Int, Int>): Boolean {
        if (state.currentPlayer.currentTile !== tile) { return false }

        if (state.board.grid.grid.containsKey(position)) { return false }

        if (isTreasureTilePosition(position)) {  return false }

        if (distanceToCenter(position) > 4) { return false }

        val rotatedTile = tile.deepCopy()
        rotatedTile.rotate(rotation)

        val gate = borderingGateNumber(position)

        if (gate != null) {
            val snd = (gate + 1) % 6

            if (rotatedTile.paths[gate] == snd) {
                return false
            }
        }

        if (tile.tileType == TileType.TREASURE_CENTER || tile.tileType == TileType.TREASURE_CORNER) { return false }

        return true
    }

    /**
     * Calculate the number of the gate bordering a given position.
     * This method is only ever used for tiles that can be placed by users,
     * eliminating the need to handle the case where two gates border
     * some position.
     * @param position position which may or may not be bordering a gate
     * @return number of the bordering gate (between 0 and 5 (inclusive)) or null
     *         if no gate is bordering the position
     */
    fun borderingGateNumber(position: Pair<Int, Int>): Int? {
        val distance = distanceToCenter(position)

        // only tiles in the outermost ring border gates
        if (distance != 4) {
            return null
        }

        // treasure tiles do not have bordering gates (or none that have to be considered)
        if (isTreasureTilePosition(position)) {
            return null
        }

        // only tiles bordering a gate remain
        val border = getOuterPositions()
        val index = border.indexOf(position)

        return index / 4
    }

    /**
     * Generate a random valid move.
     * @param state gamestate the move should be valid in
     * @throws IllegalStateException when no valid moves exist
     */
    fun calculateRandomAIMove(state: GameState): Pair<Pair<Tile, Int>, Pair<Int, Int>> {
        val tile = checkNotNull(state.currentPlayer.currentTile) { "players should be holding tiles in an active game" }

        var q: Int
        var r: Int
        var rotate: Int

        var iterCount = 0

        do {
            q = Random.nextInt(-4, 5)
            r = Random.nextInt(-4, 5)
            rotate = Random.nextInt(0, 6)

            iterCount += 1
        } while (!isValidMove(state, tile, rotate, Pair(q, r)) && iterCount < 30000)

        check(iterCount < 30000) { "no valid moves exist" }
        return Pair(Pair(tile, rotate), Pair(q, r))
    }

    /**
     * get a list of all positions of the outer hexagon ring in clockwise
     * order starting at the topmost treasure tile (0,-4)
     */
    private fun getOuterPositions(): List<Pair<Int, Int>> {
        var cur = Pair(0, -4)
        val visited: HashSet<Pair<Int, Int>> = HashSet(24)

        return (0..23).map {
            val old = cur
            visited.add(old)

            // .first fails at index 23 because all neighbours are already in visited
            if (it < 23) {
                cur = neighbouringPositions(cur).first {
                    distanceToCenter(it) == 4 && !visited.contains(it)
                }
            }

            old
        }
    }

    /**
     * Get an array of all position bordering [pos].
     * @param pos position to get neighbours of
     * @returns array of 6 positions bordering [pos]
     */
    fun neighbouringPositions(pos: Pair<Int, Int>): Array<Pair<Int, Int>> =
        arrayOf(
            Pair(0, -1), Pair(1, -1), Pair(1, 0),
            Pair(0, 1), Pair(-1, 1), Pair(-1, 0)
        ).map {
            val qNei = pos.first + it.first
            val rNei = pos.second + it.second

            Pair(qNei, rNei)
        }.toTypedArray()

    /**
     * Calculate the distance of some position to the center tile.
     * The outermost ring of hexagons has a distance of 4, the next inner
     * ring of 3 and the center tile has a distance of 0.
     * @param position Point to calculate the distance from the center from.
     * @return positive integer value
     */
    fun distanceToCenter(position: Pair<Int, Int>): Int {
        val q = position.first
        val r = position.second
        val s = -q - r

        return max(q.absoluteValue, max(r.absoluteValue, s.absoluteValue))
    }

    /** Check whether a treasure tile lies at a given position. */
    private fun isTreasureTilePosition(pos: Pair<Int, Int>): Boolean {
        val sCoordinate = -pos.first - pos.second
        val distance = distanceToCenter(pos)
        val hasZero = pos.first == 0 || pos.second == 0 || sCoordinate == 0

        return distance == 0 || (distance == 4 && hasZero)
    }
}