package com.kingsley.tetris.piece

class SPiece : Piece() {
    override val shape = "S"

    private val pieceArrays = arrayOf(
        intArrayOf(
            0, 1, 1, 0, 0,
            1, 1, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0
        ), intArrayOf(
            1, 0, 0, 0, 0,
            1, 1, 0, 0, 0,
            0, 1, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0
        )
    )


    override fun getPieceArray(): IntArray {
        return pieceArrays[state]
    }

    override fun getSimplePieceArray(): IntArray {
        initState()
        initialRow = 3
        return pieceArrays[state].copyOf(8)
    }

    override fun nextStatePieceArray(): IntArray {
        when (state) {
            0 -> state = 1
            1 -> state = 0
        }
        return getPieceArray()
    }

    override fun previousStatePieceArray(): IntArray {
        when (state) {
            0 -> state = 1
            1 -> state = 0
        }
        return getPieceArray()
    }

    override fun isCollision(column: Int): Boolean {
        return when (state) {
            0 -> column < 1 || column > BOARD_COLUMN - 2
            1 -> column < 1 || column > BOARD_COLUMN - 1
            else -> true
        }
    }

    private fun initState() {
        state = 0
    }
}