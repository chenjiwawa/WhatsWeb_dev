package com.qltech.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.annotation.LayoutRes
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.qltech.common.args.ArgsCreator
import com.qltech.common.args.putArgs
import com.qltech.common.extensions.subscribe
import com.qltech.common.utils.IntentUtils
import com.qltech.common.utils.ToastUtils
import com.qltech.common.utils.XLog
import com.qltech.ui.arguments.WebArguments
import com.qltech.ui.Configuration.WebConfiguration
import com.qltech.ui.viewmodel.IWebViewModel
import com.qltech.ui.viewmodel.WebViewModel
import kotlinx.android.synthetic.main.activity_webview.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

open class WebViewActivity(@LayoutRes layoutResId: Int = R.layout.activity_webview) : BaseActivity(layoutResId) {

    companion object {
        private const val TAG = "WebViewActivity"
        private const val JS_INTERFACE_NAME = "android"

        private const val RESULT_CODE_FILE_CHOOSER = 2
    }

    private val logTag = this::class.java.simpleName

    private val arguments: WebArguments by ArgsCreator()

    private val viewModel: IWebViewModel by viewModel<WebViewModel> { parametersOf(arguments) }

    private var mUploadCallbackAboveL: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        initWebView()
    }

    private fun initData() {
        subscribe(viewModel.titleLiveData) {
            setActionBarTitle(it)
        }
        subscribe(viewModel.urlLiveData) {
            loadUrl(it)
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun initWebView() {
        web_view.run {
            settings.run(WebConfiguration.settings)
            addJavascriptInterface(webNativeInterface, JS_INTERFACE_NAME)
            webViewClient = object : WebViewClient() {

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    handler?.proceed()
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    XLog.i(logTag, "[shouldOverrideUrlLoading] url: ${request?.url}")

                    return when (request?.url?.scheme) {
                        "http",
                        "https" -> false //不处理, 继续交由WebView负责
                        else -> {
                            IntentUtils.openUrl(this@WebViewActivity, request?.url.toString())
                            true
                        }
                    }
                }

            }
            webChromeClient = object : WebChromeClient() {

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    if (null == title) return

                    setActionBarTitle(title)
                }

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    web_progress_bar?.progress = newProgress
                    web_progress_bar?.visibility = if (newProgress >= 100) View.GONE else View.VISIBLE
                }

                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    mUploadCallbackAboveL = filePathCallback

                    IntentUtils.toImageChooser(this@WebViewActivity, RESULT_CODE_FILE_CHOOSER)
                    return true
                }

                override fun onConsoleMessage(message: ConsoleMessage?): Boolean {
                    XLog.i(TAG, "[onConsoleMessage] ${message?.message()}")
                    return super.onConsoleMessage(message)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        web_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        web_view.onPause()
    }

    override fun onNavigationClick() {
        finish()
    }

    override fun setActionBarTitle(title: String) {
        if (title.isNotBlank()) {
            super.setActionBarTitle(title)
        }
    }

    override fun onBackPressed() {
        if (web_view.canGoBack()) {
            web_view.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        web_view.run {
            removeJavascriptInterface(JS_INTERFACE_NAME)
            webChromeClient = null
            destroy()
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_CODE_FILE_CHOOSER) {
            onActivityResultAboveL(requestCode, resultCode, data)
        } else {
            if (RESULT_OK == resultCode) {
                web_view.reload()
            }
        }
    }

    fun loadUrl(url: String) {
        XLog.i(logTag, "req_tag_web: $url")
        web_view.loadUrl(url)
    }

    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != RESULT_CODE_FILE_CHOOSER) return

        val results: Array<Uri>? = if (RESULT_OK == resultCode) {
            val clipData = data?.clipData
            val dataString = data?.dataString

            when {
                clipData != null -> Array(clipData.itemCount) { clipData.getItemAt(it).uri }
                dataString != null -> arrayOf(Uri.parse(dataString))
                else -> null
            }
        } else {
            null
        }

        mUploadCallbackAboveL?.onReceiveValue(results)
        mUploadCallbackAboveL = null
    }

    private val webNativeInterface = WebNativeInterface(
        onClosePage = {
            finish()
        },
        onOpenBrowser = {
            IntentUtils.openUrl(this, it)
        },
        onOpenWebView = {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putArgs(WebArguments(it))
            IntentUtils.startActivity(this, intent)
        },
        onShowToast = {
            ToastUtils.showToast(it)
        },
        onSetTitle = {
            setActionBarTitle(it)
        }
    )

    inner class WebNativeInterface(
        private val onClosePage: () -> Unit,
        private val onOpenBrowser: (url: String) -> Unit,
        private val onOpenWebView: (url: String) -> Unit,
        private val onShowToast: (message: String) -> Unit,
        private val onSetTitle: (title: String) -> Unit
    ) {

        private val gson: Gson = Gson()

        @JavascriptInterface
        fun goBack(json: String) {
            XLog.d(TAG, "[goBack] $json")
            runOnUiThread {
                onClosePage.invoke()
            }
        }

        @JavascriptInterface
        fun openBrowser(json: String) {
            XLog.d(TAG, "[openBrowser] $json")
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            val url = jsonObject.get("url").asString

            runOnUiThread {
                onOpenBrowser.invoke(url)
            }
        }

        @JavascriptInterface
        fun openWebView(json: String) {
            XLog.d(TAG, "[openWebView] $json")
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            val url = jsonObject.get("url").asString

            runOnUiThread {
                onOpenWebView.invoke(url)
            }
        }

        @JavascriptInterface
        fun showToast(json: String) {
            XLog.d(TAG, "[showToast] $json")
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            val message = jsonObject.get("message").asString

            runOnUiThread {
                onShowToast.invoke(message)
            }
        }

        @JavascriptInterface
        fun setTitle(json: String) {
            XLog.d(TAG, "[setTitle] $json")
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            val title = jsonObject.get("title").asString

            runOnUiThread {
                onSetTitle.invoke(title)
            }
        }
    }
}