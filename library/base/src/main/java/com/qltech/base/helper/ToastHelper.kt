package com.qltech.base.helper

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

interface IToast {
    fun showToast(@StringRes stringRes: Int)
    fun showToast(message: String?)
}

internal class ToastHelper(private val context: Context) : IToast {

    private val stringHelper: IString = AppHelper.stringHelper

    override fun showToast(@StringRes stringRes: Int) {
        showToast(stringHelper.getString(stringRes))
    }

    override fun showToast(message: String?) {
        message?.run {
            createToast(this).show()
        }
    }

    private fun createToast(message: String): Toast {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT)
    }

}