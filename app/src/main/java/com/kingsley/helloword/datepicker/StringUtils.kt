package com.kingsley.helloword.datepicker

import android.text.format.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

/**
 * 字符串操作工具包
 *
 */
internal object StringUtils {
    fun conversionTime(
        time: String,
        format: String = "yyyy-MM-dd HH:mm:ss"
    ): Long {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val ofPattern = DateTimeFormatter.ofPattern(format)
            return LocalDateTime.parse(time, ofPattern).toInstant(ZoneOffset.ofHours(8))
                .toEpochMilli()
        } else {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            try {
                return sdf.parse(time)?.time?:0
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return 0
        }
    }

    /**
     * @param time
     * @return yy-MM-dd HH:mm格式时间
     */
    fun conversionTime(time: Long, format: String = "yyyy-MM-dd HH:mm:ss"): String {
        return DateFormat.format(format, time).toString()
    }

    /**
     * 根据当前日期获得是星期几
     * time=yyyy-MM-dd
     *
     * @return
     */
    fun getWeek(time: Long): String {
        val c = Calendar.getInstance()
        c.timeInMillis = time
        return when (c[Calendar.DAY_OF_WEEK]) {
            1 -> "周日"
            2 -> "周一"
            3 -> "周二"
            4 -> "周三"
            5 -> "周四"
            6 -> "周五"
            7 -> "周六"
            else -> ""
        }
    }
}