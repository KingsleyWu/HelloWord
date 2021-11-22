package com.kingsley.shimeji.data

import android.provider.BaseColumns


object BitmapEntry : BaseColumns {
    const val COLUMN_NAME_BITMAP = "bitmap"
    const val COLUMN_NAME_FRAME = "frame"
    const val COLUMN_NAME_MASCOT_ID = "mascot"
    const val TABLE_NAME = "bitmaps"
}

object MascotEntry : BaseColumns {
    const val COLUMN_NAME_NAME = "name"
    const val COLUMN_NAME_PURCHASED = "purchased"
    const val TABLE_NAME = "mascots"
}