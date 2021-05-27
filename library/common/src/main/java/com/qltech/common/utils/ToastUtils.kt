package com.qltech.common.utils

import androidx.annotation.StringRes
import com.qltech.base.helper.AppHelper
import com.qltech.base.helper.IToast

object ToastUtils {
    private val toastProvider: IToast = AppHelper.toastHelper

    fun showToast(@StringRes stringRes: Int) {
        toastProvider.showToast(stringRes)
    }

    fun showToast(message: String?) {
        toastProvider.showToast(message)
    }

    fun showErrorMessage(e: Throwable) {
        toastProvider.showToast(e.message)
    }
}