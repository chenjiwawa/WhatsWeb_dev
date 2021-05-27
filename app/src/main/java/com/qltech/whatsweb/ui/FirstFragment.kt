package com.qltech.whatsweb.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.mutlcolorloadingview.MutlColorLoadingView
import com.orhanobut.logger.Logger
import com.qltech.whatsweb.R
import com.qltech.firebase.ad.AdManager
import com.qltech.whatsweb.presenter.WaWebViewPresenter
import com.qltech.whatsweb.presenter.WebViewContract
import com.qltech.whatsweb.ui.event.FullScreenEvent
import com.qltech.whatsweb.ui.event.UserAgentEvent
import com.qltech.whatsweb.ui.callback.Unlock
import com.qltech.whatsweb.ui.callback.UnlockHome
import com.qltech.whatsweb.ui.callback.UnlockUserAgent
import com.qltech.whatsweb.ui.setting.SettingActivity
import com.qltech.whatsweb.ui.useragnet.UserAgentActivity
import com.qltech.whatsweb.util.Constants
import com.qltech.whatsweb.util.FirebaseAnalyticsRecoder
import com.qltech.whatsweb.util.RemoteConfigConstants
import kotlinx.android.synthetic.main.fragment_first.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), WebViewContract.IWebView {

    companion object {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO

        private val VIDEO_PERMISSION = arrayOf(
            CAMERA_PERMISSION,
            AUDIO_PERMISSION
        )

        private const val FILE_CHOOSER_RESULT_CODE = 200

        private const val CAMERA_PERMISSION_RESULT_CODE = 201
        private const val AUDIO_PERMISSION_RESULT_CODE = 202
        private const val VIDEO_PERMISSION_RESULT_CODE = 203
        private const val STORAGE_PERMISSION_RESULT_CODE = 204

        private const val REWARDAD_HOME = 11
        private const val REWARDAD_USERAGENT = 12

        private const val REWARDAD_STATUS_UNKNOW = 0
        private const val REWARDAD_STATUS_LOAD_SUCCESS = 1
        private const val REWARDAD_STATUS_LOAD_FAILURE = 2

        private const val TAG = "WhatsWeb"
    }

    private var mSharedPrefs: SharedPreferences? = null

    private var mMainView: ViewGroup? = null
    private var mKeyboardEnabled: Boolean = false

    private var mUploadMessage: ValueCallback<Array<Uri>>? = null
    private var mCurrentPermissionRequest: PermissionRequest? = null

    private var mPresenter: WebViewContract.IWebViewPresenter? = null

    private lateinit var interstitialAd: InterstitialAd

    private var mLoading: MutlColorLoadingView? = null

    // 激励广告实例
    private var rewardedAd: RewardedAd? = null

    // 是否挣得广告激励
    private var isEarnedReward = false

    // 激励广告加载状态
    private var rewardAdLoadStatus =
        REWARDAD_STATUS_UNKNOW

    private var currentRewardAd =
        REWARDAD_HOME

    // 激励广告加载失败信息
    private var rewardedAdLoadAdError: LoadAdError? = null

    // 是否有待显示的激励广告
    private var hasPendingShowRewardAd = false

    // 重试显示激励广告次数
    private var waitRewardAdLoadRetryCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setHasOptionsMenu(true)
        mSharedPrefs = requireActivity().getSharedPreferences(
            requireActivity().packageName,
            Context.MODE_PRIVATE
        )

        interstitialAd = InterstitialAd(context)

        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
            initInterstitialAd()
        }

        initRewardAd()
    }

    private fun initRewardAd() {
        rewardedAd = createAndLoadRewardedAd(
            AdManager.getUserAgentReWardId()
        )
    }

    private fun createAndLoadRewardedAd(adUnitId: String?): RewardedAd? {
        val rewardedAd = RewardedAd(context, adUnitId)
        val adLoadCallback: RewardedAdLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                // Ad successfully loaded.
                Log.i(TAG, "load rewarded ad finished")
                rewardAdLoadStatus =
                    REWARDAD_STATUS_LOAD_SUCCESS
                if (hasPendingShowRewardAd) {
                    when (currentRewardAd) {
                        REWARDAD_HOME -> showRewardAd(
                            UnlockHome(
                                activity
                            )
                        )
                        REWARDAD_USERAGENT -> showRewardAd(
                            UnlockUserAgent(
                                activity
                            )
                        )
                    }
                    hasPendingShowRewardAd = false
                }
            }

            override fun onRewardedAdFailedToLoad(adError: LoadAdError?) {
                // Ad failed to load.
                Log.e(TAG, "load rewarded ad failed:" + adError)
                rewardAdLoadStatus =
                    REWARDAD_STATUS_LOAD_FAILURE
                rewardedAdLoadAdError = adError
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        return rewardedAd
    }

    private fun initInterstitialAd() {
        if (context == null)
            return

        if (!(Firebase.remoteConfig[RemoteConfigConstants.AdsKey.AD_MASTER_SWITCH].asBoolean() && Firebase.remoteConfig[RemoteConfigConstants.AdsKey.AD_INTERSTITIAL_REFRESH_PAGE].asBoolean()))
            return

        interstitialAd.adUnitId = AdManager.getRefreshPageInterstitialId()
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
        interstitialAd.loadAd(AdRequest.Builder().build())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WebView(context).destroy()

        close.setOnClickListener { showFullScreen() }
        container.setOnClickListener { showFullScreen() }
        mMainView = view as ViewGroup
        mPresenter = WaWebViewPresenter(this, view.findViewById(R.id.webview))

        mLoading = view.findViewById(R.id.loading)

        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
            initBannerAd(view)
        }

        showHomeRewardAd()
    }

    private fun initBannerAd(view: View) {
        if (context == null)
            return

        if (!(Firebase.remoteConfig[RemoteConfigConstants.AdsKey.AD_MASTER_SWITCH].asBoolean() && Firebase.remoteConfig[RemoteConfigConstants.AdsKey.AD_BANNER_HOME].asBoolean()))
            return

        val adView = AdView(context)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = AdManager.getWhatsWebBannerId()

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        var frame: FrameLayout = view.findViewById(R.id.banner_ad_layout)
        frame.removeAllViews()
        frame.addView(adView)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            FirebaseAnalyticsRecoder.getInstance(context)
                .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.LOAD_WHATSAPP)

            mPresenter?.loadWhatsApp()
        } else {
            mPresenter?.onRestoreInstanceState(savedInstanceState)
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.onResume()

        mKeyboardEnabled =
            mSharedPrefs?.getBoolean(Constants.PrefKey.KEYBOARD_ENABLED, true) ?: true
        setKeyboardEnabled(mKeyboardEnabled)

//        showIntroInfo()

    }

    override fun onPause() {
        super.onPause()
        mPresenter?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        mPresenter?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mPresenter?.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.finish()
                true;
            }
            R.id.menu_reload -> {
                FirebaseAnalyticsRecoder.getInstance(context)
                    .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.RELOAD_WHATSAPP)

                mPresenter?.loadWhatsApp()
                if (interstitialAd.isLoaded) {
                    interstitialAd.show()
                }
                true
            }
            R.id.menu_logout -> {
                FirebaseAnalyticsRecoder.getInstance(context)
                    .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.OPEN_URL)

                mPresenter?.logout()
                if (interstitialAd.isLoaded) {
                    interstitialAd.show()
                }
                true
            }
            R.id.menu_useragent -> {
                showUserAgentRewardAd()
                true;
            }
            R.id.menu_fullscreen -> {
                showFullScreen()
                true
            }
            R.id.menu_toggle_keyboard -> {
                toggleKeyboard()
                true
            }
            R.id.menu_about -> {
                FirebaseAnalyticsRecoder.getInstance(context)
                    .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_SETTING)
                startActivity(Intent(context, SettingActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showRewardAdTipDialog(rewardadUnlockInfo: String, unlock: Unlock) {
        AlertDialog.Builder(context)
            .setTitle(R.string.tips)
            .setMessage(rewardadUnlockInfo)
            .setCancelable(false)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                dialog.cancel()
                showRewardAd(unlock)
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.cancel()
                unlock.onFail()
            }
            .create()
            .show()
    }

    private fun showRewardAd(unlock: Unlock) {
        when (rewardAdLoadStatus) {
            REWARDAD_STATUS_LOAD_SUCCESS -> {
                val adCallback = object : RewardedAdCallback() {
                    override fun onRewardedAdOpened() {
                        // Ad opened.
                    }

                    override fun onRewardedAdClosed() {
                        // Ad closed.
                        if (isEarnedReward) {
                            unlock.onSuccess()
                        }else{
                            unlock.onFail()
                        }
                        // 1. 已经挣得广告激励，重新加载广告，下一次需要重新查看激励视频
                        // 2. 用户提前关闭激励广告，重新加载广告，等待下一次 show
                        initRewardAd()
                        isEarnedReward = false
                    }

                    override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                        // User earned reward.
                        isEarnedReward = true
                    }

                    override fun onRewardedAdFailedToShow(adError: AdError) {
                        // Ad failed to display.
                        Log.e(TAG, "onRewardedAdFailedToShow:" + adError)
                        isEarnedReward = false
                        unlock.onSuccess()
                    }
                }
                rewardedAd?.show(activity, adCallback)
            }
            REWARDAD_STATUS_LOAD_FAILURE -> {
                parseLoadAdError(rewardedAdLoadAdError, unlock)
            }
            REWARDAD_STATUS_UNKNOW -> {
                mLoading?.visibility = View.VISIBLE
                // 激励广告还未加载完成，延时等待 10x300ms=3s
                hasPendingShowRewardAd = true
                if (waitRewardAdLoadRetryCount > 10) {
                    hasPendingShowRewardAd = false
                    mLoading?.visibility = View.GONE
                    // 超过重试次数后，直接跳转到 userAgent 设置
                    unlock.onSuccess()
                } else {
                    waitRewardAdLoadRetryCount++
                    mMainView?.postDelayed({
                        if (rewardAdLoadStatus == REWARDAD_STATUS_UNKNOW) {
                            showRewardAd(unlock)
                        } else {
                            mLoading?.visibility = View.GONE
                        }
                    }, 300)
                }
            }
        }
    }

    private fun parseLoadAdError(adError: LoadAdError?, unlock: Unlock) {
        Log.e(TAG, "RewardAd LoadAdError:" + adError)
        when (adError?.code) {
            // The ad request was unsuccessful due to network connectivity.
            AdRequest.ERROR_CODE_NETWORK_ERROR,
            AdRequest.ERROR_CODE_INTERNAL_ERROR -> {
                FirebaseAnalyticsRecoder.getInstance(context)
                    .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.REWARD_AD_LOAD_INTERNET_ERROR)
                showNetworkErrorDialog()
            }
            else -> {
                FirebaseAnalyticsRecoder.getInstance(context)
                    .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.REWARD_AD_LOAD_OTHER_ERROR)
                unlock.onSuccess()
            }
        }
    }

    private fun showNetworkErrorDialog() {
//        context ?: let {
//            NetworkTipDialog.Builder()
//                .messageTextId(R.string.network_reconnect_info)
//                .onButtonClickListener { dialog, _ ->
//                    dialog.dismiss()
//                    // reload reward ad
//                    initRewardAd()
//                }.build(it.context)?.show()
//        }
    }

    private fun toUserAgentActivity() {
        FirebaseAnalyticsRecoder.getInstance(context)
            .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_USERAGENT)
        startActivity(Intent(context, UserAgentActivity::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            VIDEO_PERMISSION_RESULT_CODE ->
                if (permissions.size == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        mCurrentPermissionRequest?.grant(mCurrentPermissionRequest?.resources)
                    } catch (e: java.lang.RuntimeException) {
                        Log.e(TAG, "Granting permissions failed", e)
                    }
                } else {
                    showSnackBar("Permission not granted, can't use video.")
                    mCurrentPermissionRequest?.deny()
                }
            CAMERA_PERMISSION_RESULT_CODE, AUDIO_PERMISSION_RESULT_CODE ->
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        mCurrentPermissionRequest?.grant(mCurrentPermissionRequest?.resources)
                    } catch (e: java.lang.RuntimeException) {
                        Log.e(TAG, "Granting permissions failed", e)
                    }
                } else {
                    showSnackBar(
                        "Permission not granted, can't use " +
                                if (requestCode == CAMERA_PERMISSION_RESULT_CODE) "camera" else "microphone"
                    )
                    mCurrentPermissionRequest?.deny()
                }
            STORAGE_PERMISSION_RESULT_CODE ->
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: check for current download and enqueue it
                } else {
                    showSnackBar("Permission not granted, can't download to storage")
                }
            else -> Log.d(
                TAG, "Got permission result with unknown request code " +
                        requestCode + " - " + listOf(*permissions).toString()
            )
        }
        mCurrentPermissionRequest = null

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun requestPermission(request: PermissionRequest) {
        if (request.resources[0] == PermissionRequest.RESOURCE_VIDEO_CAPTURE) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    CAMERA_PERMISSION
                ) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(
                    requireActivity(),
                    AUDIO_PERMISSION
                ) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    VIDEO_PERMISSION,
                    VIDEO_PERMISSION_RESULT_CODE
                )
                mCurrentPermissionRequest = request
            } else if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    CAMERA_PERMISSION
                ) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(CAMERA_PERMISSION),
                    CAMERA_PERMISSION_RESULT_CODE
                )
                mCurrentPermissionRequest = request
            } else if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    AUDIO_PERMISSION
                ) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(AUDIO_PERMISSION),
                    AUDIO_PERMISSION_RESULT_CODE
                )
                mCurrentPermissionRequest = request
            } else {
                request.grant(request.resources)
            }
        } else if (request.resources[0] == PermissionRequest.RESOURCE_AUDIO_CAPTURE) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    AUDIO_PERMISSION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                request.grant(request.resources)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(AUDIO_PERMISSION),
                    AUDIO_PERMISSION_RESULT_CODE
                )
                mCurrentPermissionRequest = request
            }
        } else {
            try {
                request.grant(request.resources)
            } catch (e: RuntimeException) {
                Log.d(TAG, "Granting permissions failed", e)
            }
        }
    }

    private fun toggleKeyboard() {
        setKeyboardEnabled(!mKeyboardEnabled)

        FirebaseAnalyticsRecoder.getInstance(context)
            .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.TOGGLE_KEYBOARD)
    }

    // TODO : Disable keyboard fail. 
    private fun setKeyboardEnabled(enable: Boolean) {
        mKeyboardEnabled = enable
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (enable && mMainView?.descendantFocusability == ViewGroup.FOCUS_BLOCK_DESCENDANTS) {
            mMainView?.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
            showSnackBar(getString(R.string.msg_keyboard_unlocked))
        } else if (!enable) {
            mMainView?.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
//            mPresenter?.requestWebViewFocus()
//            val currentFocus = requireActivity().currentFocus
            inputMethodManager.hideSoftInputFromWindow(
                mMainView?.windowToken,
                0
            )
            showSnackBar(getString(R.string.msg_keyboard_locked))
        }
        mSharedPrefs?.edit()?.putBoolean(Constants.PrefKey.KEYBOARD_ENABLED, enable)?.apply()
    }

    //TODO
    private fun showHomeRewardAd() {

        if (Firebase.remoteConfig[RemoteConfigConstants.AdsKey.AD_MASTER_SWITCH].asBoolean() && Firebase.remoteConfig[RemoteConfigConstants.AdsKey.AD_HOME_REWARD].asBoolean()) {
            currentRewardAd = REWARDAD_HOME
            showRewardAdTipDialog(
                getString(R.string.rewardad_unlock_app),
                UnlockHome(activity)
            )
        }
    }

    private fun showUserAgentRewardAd() {
        currentRewardAd = REWARDAD_USERAGENT
        showRewardAdTipDialog(
            getString(R.string.rewardad_unlock_info),
            UnlockUserAgent(activity)
        )
    }


    private var isFullScreen: Boolean = false
    private fun showFullScreen() {

        FirebaseAnalyticsRecoder.getInstance(context)
            .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_FULLSCREEN)

        Logger.d(" showFullScreen " + isFullScreen)
        EventBus.getDefault().post(FullScreenEvent(isFullScreen))

        if (isFullScreen) {
            isFullScreen = false

            close.visibility = View.GONE
            val lp: WindowManager.LayoutParams? = activity?.window?.attributes
            lp?.flags = lp?.flags?.and(WindowManager.LayoutParams.FLAG_FULLSCREEN.inv())
            activity?.window?.attributes = lp
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        } else {
            isFullScreen = true

            close.visibility = View.VISIBLE
            val lp: WindowManager.LayoutParams? = activity?.window?.attributes
            lp?.flags = lp?.flags?.or(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            activity?.window?.attributes = lp
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    private fun showIntroInfo() {
        FirebaseAnalyticsRecoder.getInstance(context)
            .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_INTROINFO)

        if (mSharedPrefs?.getBoolean(Constants.PrefKey.INTRODUCTION_HAS_SHOWN, false) != true) {
            showPopupDialog(getString(R.string.introduction))
        } else {
            return
        }
        mSharedPrefs?.edit()?.putBoolean(Constants.PrefKey.INTRODUCTION_HAS_SHOWN, true)?.apply()
    }

    private fun showPopupDialog(message: String) {
        FirebaseAnalyticsRecoder.getInstance(context)
            .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_POPUPDIALOG)

        val activity = try {
            requireActivity()
        } catch (ignore: Exception) {
            return
        }
        if (activity.isDestroyed || activity.isFinishing) return

        val msg = SpannableString(message)
        Linkify.addLinks(msg, Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES)
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(msg)
            .setCancelable(false)
            .setPositiveButton("Ok", null)
        val alert = builder.create()
        alert.show()
        alert.findViewById<TextView>(android.R.id.message).movementMethod =
            LinkMovementMethod.getInstance()
    }

    override fun onShowFileChooser(
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: WebChromeClient.FileChooserParams
    ) {
        mUploadMessage = filePathCallback
        val chooserIntent = fileChooserParams.createIntent()
        requireActivity().startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE)
    }

    override fun openUrl(url: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, url)
        requireActivity().startActivity(intent)

        FirebaseAnalyticsRecoder.getInstance(context)
            .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.OPEN_URL)
    }

    override fun showToast(msg: String) {
        FirebaseAnalyticsRecoder.getInstance(context)
            .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_TOAST)

        activity?.runOnUiThread {
            if (activity?.isDestroyed == false && activity?.isFinishing == false) {
                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun showSnackBar(msg: String) {
        FirebaseAnalyticsRecoder.getInstance(context)
            .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_POPUPDIALOG)

        activity?.runOnUiThread {
            if (activity?.isDestroyed == false && activity?.isFinishing == false) {
                val contentView =
                    activity?.findViewById<View>(android.R.id.content) ?: return@runOnUiThread
                val snackBar = Snackbar.make(contentView, msg, 900)
                snackBar.setAction(
                    "dismiss"
                ) { snackBar.dismiss() }
                snackBar.setActionTextColor(Color.parseColor("#075E54"))
                snackBar.show()
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: UserAgentEvent?) {
        Logger.d(" onMessageEvent UserAgentEvent " + event?.userAgent)
        event?.userAgent?.let {
            FirebaseAnalyticsRecoder.getInstance(context)
                .record(
                    FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_USERAGENT,
                    FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.USERAGENT_OS,
                    it
                )
        }

        mPresenter?.loadWhatsApp(event?.userAgent)

//        if (interstitialAd.isLoaded) {
//            interstitialAd.show()
//        }

    }

}
