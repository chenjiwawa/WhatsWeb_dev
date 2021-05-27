package com.qltech.whatsweb.presenter

import android.net.Uri
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient

interface WebViewContract {

    interface IWebView {
        fun requestPermission(request: PermissionRequest)
        fun onShowFileChooser(
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: WebChromeClient.FileChooserParams
        )

        fun openUrl(url: Uri)
        fun showToast(msg: String)
        fun showSnackBar(msg: String)
    }

    interface IWebViewPresenter {
        fun onResume()
        fun onPause()
        fun onDestroy()

        fun onSaveInstanceState(outState: Bundle?)
        fun onRestoreInstanceState(savedInstanceState: Bundle)

        fun logout()
        fun loadWhatsApp()
        fun loadWhatsApp(userAgent: String?)
        fun requestWebViewFocus()
    }
}
