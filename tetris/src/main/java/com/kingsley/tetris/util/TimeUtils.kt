package com.kingsley.tetris.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    val DATE_FORMAT_WHOLE = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val DATE_FORMAT_DATE = SimpleDateFormat("yyyy-MM-dd")
    val DATE_FORMAT_MINIUTE = SimpleDateFormat("HH:mm")
    fun getTime(timeInMillis: Long, dateFormat: SimpleDateFormat): String {
        return dateFormat.format(Date(timeInMillis))
    }

    @JvmStatic
    fun getDefaultTime(timeInMills: Long): String {
        val today = getTime(currentTimeInLong, DATE_FORMAT_DATE)
        val timeDate = getTime(timeInMills, DATE_FORMAT_DATE)
        return if (today == timeDate) {
            getTime(timeInMills, DATE_FORMAT_MINIUTE)
        } else {
            timeDate
        }
    }

    val currentTimeInLong: Long
        get() = System.currentTimeMillis()
}