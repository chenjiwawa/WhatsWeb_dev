package com.qltech.firebase.ad

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.qltech.WhatsWebApplication
import com.qltech.whatsweb.util.RemoteConfigConstants
import java.util.*


/** Prefetches App Open Ads.  */
class AppOpenManager(myApplication: Application) : LifecycleObserver,
    Application.ActivityLifecycleCallbacks {
    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAdLoadCallback? = null
    private val myApplication: WhatsWebApplication

    /** Request an ad  */
    fun fetchAd() {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable) {
            return
        }
        loadCallback = object : AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle the error.
            }

            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */
            override fun onAppOpenAdLoaded(ad: AppOpenAd?) {
                appOpenAd = ad
                loadTime = Date().time
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            override fun onAppOpenAdFailedToLoad(loadAdError: LoadAdError?) {
                // Handle the error.
            }
        }
        AppOpenAd.load(
            myApplication, AdManager.getHomeAppOpenId(), adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }

    /** Creates and returns ad request.  */
    private val adRequest: AdRequest
        private get() = AdRequest.Builder().build()

    /** Utility method that checks if ad exists and can be shown.  */
    val isAdAvailable: Boolean
        get() = adMasterSwitch && adHomeAppOpen && appOpenAd != null && wasLoadTimeLessThanNHoursAgo(
            4
        )//4

    private var adMasterSwitch = true
        set(value) {
            field = value
        }
    private var adHomeAppOpen = true
        set(value) {
            field = value
        }

    companion object {
        private const val LOG_TAG = "AppOpenManager"
//        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294"
    }

    /** Constructor  */
    init {
        this.myApplication = myApplication as WhatsWebApplication
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    private var currentActivity: Activity? = null

    /** ActivityLifecycleCallback methods  */
    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity?) {}

    override fun onActivityPaused(activity: Activity?) {}

    override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) {}

    override fun onActivityDestroyed(activity: Activity?) {
        currentActivity = null
    }

    private var isShowingAd = false

    /** Shows the ad if one isn't already showing.  */
    fun showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        Log.d(LOG_TAG, " showAdIfAvailable isAdAvailable "+isAdAvailable)

        if (!isShowingAd && isAdAvailable) {
            Log.d(LOG_TAG, "Will show ad.")
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null
                        isShowingAd = false
                        fetchAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {}
                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                    }
                }
            appOpenAd!!.show(currentActivity, fullScreenContentCallback)
        } else {
            Log.d(LOG_TAG, "Can not show ad.")
            fetchAd()
        }
    }

    /** LifecycleObserver methods  */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
            adMasterSwitch =
                Firebase.remoteConfig[RemoteConfigConstants.AdsKey.AD_MASTER_SWITCH].asBoolean()
            adHomeAppOpen =
                Firebase.remoteConfig[RemoteConfigConstants.AdsKey.AD_HOME_APP_OPEN].asBoolean()
            showAdIfAvailable()
        }

        Log.d(LOG_TAG, "onStart")
    }

    private var loadTime: Long = 0

    /** Utility method to check if ad was loaded more than n hours ago.  */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().getTime() - this.loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }
}