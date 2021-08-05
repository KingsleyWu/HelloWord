package com.kingsley.tetris.piece

class IPiece : Piece() {
    override val shape = "I"
    private val pieceArrays = arrayOf(
        intArrayOf(
            0, 1, 0, 0, 0,
            0, 1, 0, 0, 0,
            0, 1, 0, 0, 0,
            0, 1, 0, 0, 0,
            0, 0, 0, 0, 0
        ), intArrayOf(
            0, 0, 0, 0, 0,
            1, 1, 1, 1, 0,
            0, 0, 0, 0, 0,
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
            0 -> column < 0 || column > BOARD_COLUMN - 1
            1 -> column < 1 || column > BOARD_COLUMN - 3
            else -> true
        }
    }

    private fun initState() {
        state = 1
    }
}