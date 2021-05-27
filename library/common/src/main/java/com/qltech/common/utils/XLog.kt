package com.qltech.common.utils

import android.util.Log
import com.qltech.common.BuildConfig
import com.qltech.common.helper.LogFile

object XLog {
    var logFile: LogFile? = null

    fun init() {
        if (null == logFile) logFile = LogFile()
    }

    fun v(tag: String, message: String) {
        log(Log.VERBOSE, tag, message)
    }

    fun v(tag: String, message: String, e: Throwable) {
        log(Log.VERBOSE, tag, message, e)
    }

    fun d(tag: String, message: String) {
        log(Log.DEBUG, tag, message)
    }

    fun d(tag: String, message: String, e: Throwable) {
        log(Log.DEBUG, tag, message, e)
    }

    fun i(tag: String, message: String) {
        log(Log.INFO, tag, message)
    }

    fun i(tag: String, message: String, e: Throwable) {
        log(Log.INFO, tag, message, e)
    }

    fun w(tag: String, message: String) {
        log(Log.WARN, tag, message)
    }

    fun w(tag: String, message: String, e: Throwable) {
        log(Log.WARN, tag, message, e)
    }

    fun e(tag: String, message: String) {
        log(Log.ERROR, tag, message)
    }

    fun e(tag: String, message: String, e: Throwable) {
        log(Log.ERROR, tag, message, e)
    }

    fun stackTrace(tag: String) {
        i(tag, "[LogStack] ----------------Start----------------")
        val te = Exception().stackTrace
        for (e in te) {
            i(tag, "[LogStack] $e")
        }
        i(tag, "[LogStack] ----------------End----------------")
    }

    private fun log(priority: Int, tag: String, message: String, e: Throwable? = null) {
        logFile?.saveLog(priority, tag, message)

        if (!BuildConfig.DEBUG && priority < Log.ERROR) return

        if (null == e) {
            Log.println(priority, tag, message)
        } else {
            when (priority) {
                Log.VERBOSE -> Log.v(tag, message, e)
                Log.DEBUG -> Log.d(tag, message, e)
                Log.INFO -> Log.i(tag, message, e)
                Log.WARN -> Log.w(tag, message, e)
                Log.ERROR -> Log.e(tag, message, e)
            }
        }
    }

}




