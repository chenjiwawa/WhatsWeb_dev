package com.qltech.ui.Configuration

import android.webkit.WebSettings
import com.qltech.base.helper.AppHelper
import com.qltech.base.helper.IFile

object WebConfiguration {
    private const val CACHE_FILE_NAME = "web"

    private const val CACHE_SIZE = 10L * 1024 * 1024

    private val fileProvider: IFile = AppHelper.fileHelper

    val settings: WebSettings.() -> Unit = {
        setSupportZoom(true)
        builtInZoomControls = true
        displayZoomControls = false
        javaScriptEnabled = true

        setAppCacheMaxSize(CACHE_SIZE)
        setAppCachePath(fileProvider.getCacheFile(CACHE_FILE_NAME).path)
        setAppCacheEnabled(true)
    }
}