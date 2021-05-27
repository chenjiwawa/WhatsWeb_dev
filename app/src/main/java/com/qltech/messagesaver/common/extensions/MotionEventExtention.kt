package com.qltech.messagesaver.common.extensions

import android.graphics.PointF
import android.view.MotionEvent

fun MotionEvent.toPointF(pointerIndex: Int? = null): PointF {
    return if (null == pointerIndex) {
        PointF(x, y)
    } else {
        PointF(getX(pointerIndex), getY(pointerIndex))
    }
}