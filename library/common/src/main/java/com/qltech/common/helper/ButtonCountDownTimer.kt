package com.qltech.common.helper

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.widget.TextView

class ButtonCountDownTimer(
    private val button: TextView,
    countSec: Int = 10
) : CountDownTimer(countSec * 1000L, 1000) {

    private val buttonText: String = button.text.toString()

    fun startWithButton(): CountDownTimer {
        button.isEnabled = false
        return start()
    }

    @SuppressLint("SetTextI18n")
    override fun onTick(millisUntilFinished: Long) {
        button.text = "$buttonText(${(millisUntilFinished / 1000)})"
    }

    override fun onFinish() {
        button.isEnabled = true
        button.text = buttonText
    }

}