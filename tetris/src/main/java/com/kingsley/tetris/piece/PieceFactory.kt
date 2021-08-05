package com.kingsley.tetris.piece

import java.util.*

object PieceFactory {
    private val pieceList = listOf(
        //IPiece(), JPiece(), LPiece(), OPiece(), SPiece(), TPiece(), ZPiece(),
        TestPiece())

    fun createPiece(): Piece {
        return pieceList[0]
    }

    @JvmStatic
    fun createPiece(shape: String?, state: Int): Piece {
        val piece = when (shape) {
//            "J" -> JPiece()
//            "L" -> LPiece()
//            "O" -> OPiece()
//            "S" -> SPiece()
//            "T" -> TPiece()
//            "Z" -> ZPiece()
//            "I" -> IPiece()
            "C" -> TestPiece()
            else -> TestPiece()
        }
        piece.state = state
        return piece
    }
}