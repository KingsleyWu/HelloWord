package com.kingsley.tetris.bean

data class StateBean(
    /** 方块片左下角在整个界面的行和列 */
    var row: Int = 0,
    var column: Int = 0,
    /** 界面中的方块片数组 */
    var currentPieceArray: IntArray? = null,
    var currentShape: String? = null,
    var currentState: Int = 0,
    /** “下一个”方块片数组 */
    var nextPieceArray: IntArray? = null,
    var nextShape: String? = null,
    var nextState: Int = 0,
    /** 已经确定的（不含空中方块片）的界面数组 */
    var blockBoardArray: IntArray? = null,
    /** 用于更新界面的数组（可含空中方块片，也可不含空中方块片） */
    var tempBlockBoardArray: IntArray? = null,
    /** 等级 */
    var level: Int = 1,
    /** 分数 */
    var score: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StateBean

        if (row != other.row) return false
        if (column != other.column) return false
        if (currentPieceArray != null) {
            if (other.currentPieceArray == null) return false
            if (!currentPieceArray.contentEquals(other.currentPieceArray)) return false
        } else if (other.currentPieceArray != null) return false
        if (currentShape != other.currentShape) return false
        if (currentState != other.currentState) return false
        if (nextPieceArray != null) {
            if (other.nextPieceArray == null) return false
            if (!nextPieceArray.contentEquals(other.nextPieceArray)) return false
        } else if (other.nextPieceArray != null) return false
        if (nextShape != other.nextShape) return false
        if (nextState != other.nextState) return false
        if (blockBoardArray != null) {
            if (other.blockBoardArray == null) return false
            if (!blockBoardArray.contentEquals(other.blockBoardArray)) return false
        } else if (other.blockBoardArray != null) return false
        if (tempBlockBoardArray != null) {
            if (other.tempBlockBoardArray == null) return false
            if (!tempBlockBoardArray.contentEquals(other.tempBlockBoardArray)) return false
        } else if (other.tempBlockBoardArray != null) return false
        if (level != other.level) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = row
        result = 31 * result + column
        result = 31 * result + (currentPieceArray?.contentHashCode() ?: 0)
        result = 31 * result + (currentShape?.hashCode() ?: 0)
        result = 31 * result + currentState
        result = 31 * result + (nextPieceArray?.contentHashCode() ?: 0)
        result = 31 * result + (nextShape?.hashCode() ?: 0)
        result = 31 * result + nextState
        result = 31 * result + (blockBoardArray?.contentHashCode() ?: 0)
        result = 31 * result + (tempBlockBoardArray?.contentHashCode() ?: 0)
        result = 31 * result + level
        result = 31 * result + score
        return result
    }
}