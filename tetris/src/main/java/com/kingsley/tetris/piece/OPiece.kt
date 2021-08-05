package com.kingsley.tetris.piece

class OPiece : Piece() {
    override val shape = "O"

    private val pieceArrays = arrayOf(
        intArrayOf(
            0, 0, 0, 0, 0,
            0, 1, 1, 0, 0,
            0, 1, 1, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0
        )
    )

    override fun getPieceArray(): IntArray {
        return pieceArrays[state]
    }

    override fun nextStatePieceArray(): IntArray {
        return getPieceArray()
    }

    override fun previousStatePieceArray(): IntArray {
        return getPieceArray()
    }

    override fun isCollision(column: Int): Boolean {
        return column < 0 || column > BOARD_COLUMN - 2
    }

    override fun getSimplePieceArray(): IntArray {
        initialRow = 2
        return pieceArrays[state].copyOfRange(4, 12)
    }
}