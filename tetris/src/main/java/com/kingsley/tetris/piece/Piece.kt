package com.kingsley.tetris.piece

abstract class Piece {
    open var state = 0

    /** 在整个界面上初始行初始列（即方块片左下角在整个界面的位置）  */
    var initialRow = 1
    val initialColumn = 5

    /**
     * @return 代表一个方块片的数组
     */
    abstract fun getPieceArray(): IntArray?

    /**
     * @return 即代表方块片简化后的数组
     */
    abstract fun getSimplePieceArray(): IntArray?

    /**
     * @return 方块片下一个形态的数组
     */
    abstract fun nextStatePieceArray(): IntArray?

    /**
     * @return 方块片前一个形态的数组
     */
    abstract fun previousStatePieceArray(): IntArray?
    abstract fun isCollision(column: Int): Boolean
    abstract val shape: String?

    companion object {
        /** 界面的行数和列数  */
        const val BOARD_ROW = 10
        const val BOARD_COLUMN = 10

        /** 方块片的行数和列数  */
        const val PIECE_ROW = 5
        const val PIECE_COLUMN = 5
    }
}