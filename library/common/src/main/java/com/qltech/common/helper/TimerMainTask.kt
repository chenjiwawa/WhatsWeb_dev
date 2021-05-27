package com.qltech.common.helper

import android.os.Handler
import java.util.*

abstract class TimerMainTask : TimerTask() {
    private val uiHandler = Handler()

    override fun run() {
        uiHandler.post(mainRunnable)
    }

    override fun cancel(): Boolean {
        uiHandler.removeCallbacks(mainRunnable)
        return super.cancel()
    }

    private val mainRunnable = Runnable {
        runOnMainThread()
    }

    abstract fun runOnMainThread()

}