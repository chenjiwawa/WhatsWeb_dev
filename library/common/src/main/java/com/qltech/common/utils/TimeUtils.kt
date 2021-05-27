package com.qltech.common.utils

import com.qltech.common.R
import com.qltech.base.helper.AppHelper
import com.qltech.base.helper.IString

object TimeUtils {

    private const val TIME_MILLIS_IN_SEC: Long = 1 * 1000
    private const val TIME_MILLIS_IN_MIN: Long = 60 * TIME_MILLIS_IN_SEC
    private const val TIME_MILLIS_IN_HOUR: Long = 60 * TIME_MILLIS_IN_MIN
    private const val TIME_MILLIS_IN_DAY: Long = 24 * TIME_MILLIS_IN_HOUR

    private val stringProvider: IString = AppHelper.stringHelper

    fun currentTimeMillis(): Long = System.currentTimeMillis()

    fun formatCountdownString(countdownTime: Long): String {
        val day = countdownTime / TIME_MILLIS_IN_DAY
        val hour = (countdownTime % TIME_MILLIS_IN_DAY) / TIME_MILLIS_IN_HOUR
        return if (day > 0) {
            "$day ${stringProvider.getString(R.string.time_day)} $hour ${stringProvider.getString(R.string.time_hour)}"
        } else {
            val min = (countdownTime % TIME_MILLIS_IN_HOUR) / TIME_MILLIS_IN_MIN
            if (hour > 0) {
                "$hour ${stringProvider.getString(R.string.time_hour)} $min ${stringProvider.getString(
                    R.string.time_min
                )}"
            } else {
                val sec = (countdownTime % TIME_MILLIS_IN_MIN) / TIME_MILLIS_IN_SEC
                if (min > 0) {
                    "$min ${stringProvider.getString(R.string.time_min)} $sec ${stringProvider.getString(
                        R.string.time_sec
                    )}"
                } else {
                    "$sec ${stringProvider.getString(R.string.time_sec)}"
                }
            }
        }
    }

}