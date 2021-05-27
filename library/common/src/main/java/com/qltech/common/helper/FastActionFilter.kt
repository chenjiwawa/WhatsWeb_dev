package com.qltech.common.helper

import com.qltech.common.utils.TimeUtils

class FastActionFilter {

    companion object {
        private const val INTERVAL_TIME = 1000
    }

    private var lastClickTime: Long = 0

    fun isValidAction(): Boolean {
        val currentTime = TimeUtils.currentTimeMillis()
        val isEffectiveAction = lastClickTime + INTERVAL_TIME < currentTime
        if (isEffectiveAction) {
            lastClickTime = currentTime
        }
        return isEffectiveAction
    }

}