package service

import entity.*

class AIService(val rootService: RootService) {

    fun calculateNextMove(): Pair<Tile, Int> {
        val game = rootService.currentGame ?: throw IllegalStateException("Game not initialized")

        //calculate all possible moves
        val possibleMoves = calculatePossibleMoves(game)

        //select a random move
        val selectedMove = selectRandomMove(possibleMoves)

        return selectedMove

    }

    private fun calculatePossibleMoves(game: GameState): List <Pair<Tile,Int>> {

        // get current game state, identify empty positions on the board and available tiles.
        // create a list of every valid tile and rotation combination
        return TODO("Implement calculation of possible moves")
    }

    private fun selectRandomMove(possibleMoves: List<Pair<Tile, Int>>): Pair<Tile,Int>{
        //Randomly select a move from the list of possible moves.
        return TODO("Implement selection of a random move")
    }

}