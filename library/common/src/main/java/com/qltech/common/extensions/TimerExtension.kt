package com.qltech.common.extensions

import com.qltech.common.helper.TimerMainTask
import java.util.*

/**
 * Schedules an [action] to be executed after the specified [delay] (expressed in milliseconds).
 */
inline fun Timer.scheduleMain(delay: Long, crossinline action: TimerTask.() -> Unit): TimerTask {
    val task = timerTaskMain(action)
    schedule(task, delay)
    return task
}

/**
 * Schedules an [action] to be executed at the specified [time].
 */
inline fun Timer.scheduleMain(time: Date, crossinline action: TimerTask.() -> Unit): TimerTask {
    val task = timerTaskMain(action)
    schedule(task, time)
    return task
}

/**
 * Schedules an [action] to be executed periodically, starting after the specified [delay] (expressed
 * in milliseconds) and with the interval of [period] milliseconds between the end of the previous task
 * and the start of the next one.
 */
inline fun Timer.scheduleMain(
    delay: Long,
    period: Long,
    crossinline action: TimerTask.() -> Unit
): TimerTask {
    val task = timerTaskMain(action)
    schedule(task, delay, period)
    return task
}

/**
 * Schedules an [action] to be executed periodically, starting at the specified [time] and with the
 * interval of [period] milliseconds between the end of the previous task and the start of the next one.
 */
inline fun Timer.scheduleMain(
    time: Date,
    period: Long,
    crossinline action: TimerTask.() -> Unit
): TimerTask {
    val task = timerTaskMain(action)
    schedule(task, time, period)
    return task
}

/**
 * Schedules an [action] to be executed periodically, starting after the specified [delay] (expressed
 * in milliseconds) and with the interval of [period] milliseconds between the start of the previous task
 * and the start of the next one.
 */
inline fun Timer.scheduleAtFixedRateMain(
    delay: Long,
    period: Long,
    crossinline action: TimerTask.() -> Unit
): TimerTask {
    val task = timerTaskMain(action)
    scheduleAtFixedRate(task, delay, period)
    return task
}

/**
 * Wraps the specified [action] in a [TimerTask].
 */
inline fun timerTaskMain(crossinline action: TimerTask.() -> Unit): TimerTask =
    object : TimerMainTask() {
        override fun runOnMainThread() = action()
    }