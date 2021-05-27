package com.qltech.messagesaver.common.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeUtils {

    private val dateMatch: HashMap<Int, String> = hashMapOf(
        1 to "Jan",
        2 to "Feb",
        3 to "Mar",
        4 to "Apr",
        5 to "May",
        6 to "Jun",
        7 to "Jul",
        8 to "Aug",
        9 to "Sep",
        10 to "Oct",
        11 to "Nov",
        12 to "Dec"
    )


    @JvmStatic
    fun getDuration(time: Long): String {
        val duration = System.currentTimeMillis() - time

        return when {
            duration < TimeUnit.MINUTES.toMillis(1) -> "just now"
            duration < TimeUnit.HOURS.toMillis(1) && (duration / 1000 / 60).toInt() == 1 -> (duration / 1000 / 60).toString() + " min ago"
            duration < TimeUnit.HOURS.toMillis(1) && (duration / 1000 / 60).toInt() > 1 -> (duration / 1000 / 60).toString() + " mins ago"
            duration < TimeUnit.DAYS.toMillis(1) && (duration / 1000 / 60 / 60).toInt() == 1 -> (duration / 1000 / 60 / 60).toString() + " hour ago"
            duration < TimeUnit.DAYS.toMillis(1) && (duration / 1000 / 60 / 60).toInt() > 1 -> (duration / 1000 / 60 / 60).toString() + " hours ago"
            duration <= TimeUnit.DAYS.toMillis(30) && duration >= TimeUnit.DAYS.toMillis(1) -> "${dateMatch[getDateToCalendar(time).get(Calendar.MONTH)]} ${getDateToCalendar(time).get(Calendar.DAY_OF_MONTH)} at ${getDateToString(time,"hh:mm a")}"
            else ->  "${dateMatch[getDateToCalendar(time).get(Calendar.MONTH)]} ${getDateToCalendar(time).get(Calendar.DAY_OF_MONTH)}, ${getDateToCalendar(time).get(Calendar.YEAR)}"
        }
    }

    /**
     * 时间戳转换成字符窜
     * @param milSecond
     * @param pattern
     * @return
     */
    @JvmStatic
    fun getDateToString(milSecond: Long, pattern: String): String {
        val date = Date(milSecond)
        val format = SimpleDateFormat(pattern, Locale.ENGLISH)
        return format.format(date)
    }

    /**
     * 时间戳转换成Calendar
     * @param milSecond
     * @param pattern
     * @return
     */
    @JvmStatic
    fun getDateToCalendar(milSecond: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = Date(milSecond)
        return calendar
    }
}