package com.qltech.whatsweb.presenter

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import com.orhanobut.logger.Logger
import com.qltech.whatsweb.util.LanguageUtil
import com.qltech.whatsweb.util.SpUtil
import com.qltech.whatsweb.util.UserAgentConstants

class WaWebViewPresenter(
    private val mView: WebViewContract.IWebView,
    private val mWebView: WebView
) : WebViewContract.IWebViewPresenter {


    companion object {
        private const val CHROME_FULL =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36"
        private const val USER_AGENT = CHROME_FULL

        private const val WHATSAPP_HOMEPAGE_URL = "https://www.whatsapp.com/"

        private const val WHATSAPP_WEB_BASE_URL = "web.whatsapp.com"
        private const val WORLD_ICON = "\uD83C\uDF10"

        @SuppressLint("ConstantLocale")
        private val WHATSAPP_WEB_URL = ("https://" + WHATSAPP_WEB_BASE_URL
                + "/" + WORLD_ICON + "/")

        private const val DEBUG_TAG = "WhatsWeb"

        fun getLanguage(): String? {
            var languageSimplified: String? =
                SpUtil.decodeString(SpUtil.Key.KEY_DEFAULT_LANGUAGE_SIMPLIFIED)
            var countrySimplified: String? =
                SpUtil.decodeString(SpUtil.Key.KEY_DEFAULT_COUNTRY_SIMPLIFIED)

            Logger.d(" getLanguage languageSimplified " + languageSimplified + " countrySimplified " + countrySimplified);
            if (!(languageSimplified?.isBlank()!!)) {
                return languageSimplified;
            }

            return LanguageUtil.getSystemDefaultLocale()?.language
        }
    }

    init {
        mWebView.settings.javaScriptEnabled = true //for wa web

        mWebView.settings.allowContentAccess = true // for camera

        mWebView.settings.allowFileAccess = true
        mWebView.settings.allowFileAccessFromFileURLs = true
        mWebView.settings.allowUniversalAccessFromFileURLs = true
        mWebView.settings.mediaPlaybackRequiresUserGesture = false //for audio messages


        mWebView.settings.domStorageEnabled = true //for html5 app

        mWebView.settings.databaseEnabled = true
        mWebView.settings.setAppCacheEnabled(false) // deprecated

        mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true

        mWebView.settings.setSupportZoom(true)
        mWebView.settings.builtInZoomControls = true
        mWebView.settings.displayZoomControls = false

        mWebView.settings.saveFormData = true
        mWebView.settings.loadsImagesAutomatically = true
        mWebView.settings.blockNetworkImage = false
        mWebView.settings.blockNetworkLoads = false
        mWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        mWebView.settings.setNeedInitialFocus(false)
        mWebView.settings.setGeolocationEnabled(true)
        mWebView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

        mWebView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        mWebView.isScrollbarFadingEnabled = true

        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView,
                dialog: Boolean,
                userGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                mView.showToast("OnCreateWindow")
                return true
            }

            override fun onPermissionRequest(request: PermissionRequest) {
                mView.requestPermission(request)

            }

            override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
                Log.d(DEBUG_TAG, "WebView console message: " + cm.message())
                return super.onConsoleMessage(cm)
            }

            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                mView.onShowFileChooser(filePathCallback, fileChooserParams)
                return true
            }
        }

        mWebView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                view.scrollTo(0, 0)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url
                Log.d(DEBUG_TAG, url.toString())
                return if (url.toString() == WHATSAPP_HOMEPAGE_URL) {
                    // when whatsapp somehow detects that waweb is running on a phone (e.g. trough
                    // the user agent, but apparently somehow else), it automatically redicts to the
                    // WHATSAPP_HOMEPAGE_URL. It's higly unlikely that a user wants to visit the
                    // WHATSAPP_HOMEPAGE_URL from within waweb.
                    // -> block the request and reload waweb
                    mView.showToast("WA Web has to be reloaded to keep the app running")
                    loadWhatsApp()
                    true
                } else if (url.host == WHATSAPP_WEB_BASE_URL) {
                    // whatsapp web request -> fine
                    super.shouldOverrideUrlLoading(view, request)
                } else {
                    mView.openUrl(url)
                    true
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                val msg = String.format("Error: %s - %s", error.errorCode, error.description)
                Log.d(DEBUG_TAG, msg)
            }

            override fun onUnhandledKeyEvent(view: WebView, event: KeyEvent) {
                Log.d(DEBUG_TAG, "Unhandled key event: $event")
            }
        }
    }

    override fun onResume() {
        mWebView.onResume()
    }

    override fun onPause() {
        mWebView.onPause()
    }

    override fun onDestroy() {
        mWebView.destroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        mWebView.saveState(outState!!)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        mWebView.restoreState(savedInstanceState)
    }

    override fun logout() {
        mWebView.loadUrl("javascript:localStorage.clear()")
        WebStorage.getInstance().deleteAllData()
        loadWhatsApp()
    }

//    Mozilla/5.0 (Linux; Android 9; Redmi Note 8 Build/PKQ1.190616.001; wv)
//    AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0
//    Chrome/89.0.4389.105 Mobile Safari/537.36

//    Mozilla/5.0 (Windows NT 10.0; Win64; x64)
//    AppleWebKit/537.36 (KHTML, like Gecko)
//    Chrome/75.0.3770.100 Safari/537.36

//    Mozilla/5.0 (Linux;)
//    AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0
//    Chrome/89.0.4389.105 Safari/537.36

    override fun loadWhatsApp() {
//        loadWhatsApp(UserAgentConstants.UserAngent.DEFAULT)
        loadWhatsApp(CHROME_FULL)
    }

    override fun loadWhatsApp(userAgent: String?) {
        mWebView.settings.userAgentString = userAgent
        Logger.d(" loadWhatsAppWithUA- " + mWebView.settings.userAgentString)

        var languageSimplified: String? =
            SpUtil.decodeString(SpUtil.Key.KEY_DEFAULT_LANGUAGE_SIMPLIFIED)
        var countrySimplified: String? =
            SpUtil.decodeString(SpUtil.Key.KEY_DEFAULT_COUNTRY_SIMPLIFIED)

        Logger.d(" WHATSAPP_WEB_URL " + WHATSAPP_WEB_URL + getLanguage())
        Logger.d(" WHATSAPP_WEB_URL languageSimplified " + languageSimplified)
        Logger.d(" WHATSAPP_WEB_URL countrySimplified " + countrySimplified)
        Logger.d(" WHATSAPP_WEB_URL SystemDefaultLocale " + LanguageUtil.getSystemDefaultLocale()?.language)
        mWebView.loadUrl(WHATSAPP_WEB_URL + getLanguage())
    }

    fun loadWhatsAppWithUA(os: String?) {
        Logger.d(" -loadWhatsAppWithUA os " + os)
        Logger.d(" -loadWhatsAppWithUA " + mWebView.settings.userAgentString)

        var userAgentOS: String

        when (os) {
            UserAgentConstants.OS.MAC -> userAgentOS = UserAgentConstants.UserAngentOS.MAC
            UserAgentConstants.OS.WINDOW -> userAgentOS = UserAgentConstants.UserAngentOS.WINDOW
            else -> {
                userAgentOS = UserAgentConstants.UserAngentOS.MAC
            }
        }

        mWebView.settings.userAgentString = "   Mozilla/5.0 " + userAgentOS + "" +
                " AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0" +
                " Chrome/89.0.4389.105 Safari/537.36"
        Logger.d(" loadWhatsAppWithUA- os " + os)
        Logger.d(" loadWhatsAppWithUA- " + mWebView.settings.userAgentString)

        Logger.d(" WHATSAPP_WEB_URL " + WHATSAPP_WEB_URL)
        mWebView.loadUrl(WHATSAPP_WEB_URL)

    }

    override fun requestWebViewFocus() {
        mWebView.rootView.requestFocus()
    }


}
