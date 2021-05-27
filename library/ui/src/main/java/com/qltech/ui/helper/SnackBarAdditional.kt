package com.qltech.ui.helper

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.qltech.common.extensions.subscribe
import com.qltech.common.helper.ISingleLiveObserver
import com.qltech.base.helper.AppHelper
import com.qltech.ui.R
import com.qltech.ui.model.SnackMessage

interface SnackBarAdditional {

    fun AppCompatActivity.bindSnackBar(
        snackBarMessageLiveData: ISingleLiveObserver<SnackMessage>
    ) {
        subscribe(snackBarMessageLiveData) {
            showSnackBar(it)
        }
    }

    fun AppCompatActivity.showSnackBar(bean: SnackMessage) {
        showSnackBar(bean, findViewById<ViewGroup>(R.id.container))
    }

    fun Fragment.bindSnackBar(
        snackBarMessageLiveData: ISingleLiveObserver<SnackMessage>
    ) {
        subscribe(snackBarMessageLiveData) {
            showSnackBar(it)
        }
    }

    fun Fragment.showSnackBar(bean: SnackMessage) {
        showSnackBar(bean, view)
    }

    fun AppCompatActivity.bindSnackBar(
        snackBarMessageLiveData: ISingleLiveObserver<SnackMessage>,
        container: View
    ) {
        subscribe(snackBarMessageLiveData) {
            showSnackBar(it, container)
        }
    }

    fun Fragment.showSnackBar(bean: SnackMessage, action:String, listener: View.OnClickListener) {
        showSnackBar(view, bean, action, listener)
    }

    fun AppCompatActivity.showSnackBar(bean: SnackMessage, action:String, listener: View.OnClickListener) {
        showSnackBar(findViewById<ViewGroup>(R.id.container), bean, action, listener)
    }

    fun showSnackBar(container: View?, bean: SnackMessage, action:String, listener: View.OnClickListener) {
        if (null == container) return

        val colorProvider = AppHelper.colorHelper
        var snackbar = Snackbar
            .make(container, bean.message, Snackbar.LENGTH_INDEFINITE)
            .setBackgroundTint(bean.getBackgroundColorRes())
            .setTextColor(colorProvider.getColor(R.color.text_light))
        snackbar.setAction(action, listener)
        snackbar.setActionTextColor(colorProvider.getColor(R.color.text_light))
        snackbar.show()
    }


    fun Fragment.bindSnackBar(
        snackBarMessageLiveData: ISingleLiveObserver<SnackMessage>,
        container: View
    ) {
        subscribe(snackBarMessageLiveData) {
            showSnackBar(it, container)
        }
    }

    fun showSnackBar(bean: SnackMessage, container: View?) {
        if (null == container) return

        val colorProvider = AppHelper.colorHelper
        Snackbar
            .make(container, bean.message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(bean.getBackgroundColorRes())
            .setTextColor(colorProvider.getColor(R.color.text_light))
            .show()
    }

    private fun SnackMessage.getBackgroundColorRes(): Int {
        val colorProvider = AppHelper.colorHelper
        return colorProvider.getColor(
            when (type) {
                SnackMessage.Type.ERROR -> R.color.error
                else -> R.color.colorPrimary
            }
        )
    }
}