package com.qltech.firebase.helper

import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import com.qltech.base.helper.AppHelper
import com.qltech.messagesaver.common.extensions.toPointF
import kotlin.math.abs

class ClickTouchListener(private val onClick: () -> Unit) : View.OnTouchListener {

    companion object {
        private const val INTERVAL_CLICK = 300
        private val CLICK_RANGE = AppHelper.dimensionHelper.getDensity() * 3
    }

    private var lastDownTime: Long = 0
    private var firstPoint: PointF? = null

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastDownTime = System.currentTimeMillis()
                firstPoint = event.toPointF()
            }
            MotionEvent.ACTION_UP -> {
                if (isClick(event.toPointF())) {
                    onClick()
                    v.performClick()
                }
            }
        }
        return false
    }

    private fun isClick(currentPoint: PointF): Boolean {
        val startPoint = firstPoint ?: return false
        if (System.currentTimeMillis() - lastDownTime > INTERVAL_CLICK) return false

        return abs(currentPoint.x - startPoint.x) < CLICK_RANGE &&
            abs(currentPoint.y - startPoint.y) < CLICK_RANGE
    }

}

fun View.setOnClickTouchListener(onClick: () -> Unit) {
    setOnTouchListener(ClickTouchListener(onClick))
}