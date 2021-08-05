package com.kingsley.tetris.piece

import java.util.*

/**
 * @author Kingsley
 * Created on 2021/7/20.
 */
class TestPiece() : Piece(){
    override val shape = "C"

    private val pieceArrays = arrayOf(
        intArrayOf(
            1, 1, 1, 1, 1,
            1, 0, 0, 0, 0,
            1, 1, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0
        ), intArrayOf(
            1, 1, 1, 0, 0,
            1, 0, 1, 0, 0,
            0, 0, 1, 0, 0,
            0, 0, 1, 0, 0,
            0, 0, 1, 0, 0
        ), intArrayOf(
            0, 0, 0, 1, 1,
            0, 0, 0, 0, 1,
            1, 1, 1, 1, 1,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0
        ), intArrayOf(
            1, 1, 1, 0, 0,
            1, 0, 1, 0, 0,
            1, 0, 0, 0, 0,
            1, 0, 0, 0, 0,
            1, 0, 0, 0, 0
        )
    )

    override fun getPieceArray(): IntArray {
        return pieceArrays[state]
    }

    override fun getSimplePieceArray(): IntArray {
        initState()
        var simpleBlockShape = IntArray(10)

        initialRow = 4
        when (state) {
            1 -> {
                simpleBlockShape = pieceArrays[state].copyOf(10)
            }
            3 -> {
                simpleBlockShape = pieceArrays[state].copyOfRange(5, 15)
            }
        }
        return simpleBlockShape
    }

    override fun nextStatePieceArray(): IntArray {
        when (state) {
            0 -> state = 1
            1 -> state = 2
            2 -> state = 3
            3 -> state = 0
        }
        return getPieceArray()
    }

    override fun previousStatePieceArray(): IntArray {
        when (state) {
            0 -> state = 3
            1 -> state = 0
            2 -> state = 1
            3 -> state = 2
        }
        return getPieceArray()
    }

    override fun isCollision(column: Int): Boolean {
        return when (state) {
            0 -> column < 1 || column > BOARD_COLUMN - 1
            1 -> column < 1 || column >= BOARD_COLUMN - 2
            2 -> column < 0 || column > BOARD_COLUMN - 2
            3 -> column < 1 || column > BOARD_COLUMN - 2
            else -> true
        }
    }

    private fun initState() {
        val random = Random()
        when (random.nextInt(2)) {
            0 -> state = 1
            1 -> state = 3
        }
    }
}