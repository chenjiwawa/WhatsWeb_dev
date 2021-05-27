package com.qltech

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.flurry.android.FlurryAgent
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.jeremy.fastsharedpreferences.FastSharedPreferences
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.qltech.base.BaseApplication
import com.qltech.common.utils.AppTracker
import com.qltech.common.utils.XLog
import com.qltech.messagesaver.*
import com.qltech.firebase.RemoteConfigManager
import com.qltech.firebase.ad.saver.AdManager
import com.qltech.firebase.remoteconfig.AdRemoteConfig
import com.qltech.firebase.ad.AppOpenManager
import com.qltech.whatsweb.BuildConfig
import com.qltech.whatsweb.R
import com.qltech.whatsweb.util.FirebaseAnalyticsRecoder
import com.qltech.whatsweb.util.LanguageUtil
import com.qltech.whatsweb.util.SpUtil
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.*


class WhatsWebApplication : BaseApplication() {

    private var topActivity: Activity? = null
    private var switchCount: Int = 0
    private var interstitialAd: InterstitialAd = InterstitialAd(this)

    var isBackground = false

    companion object {
        private lateinit var instance: BaseApplication

        internal fun getAppContext(): Context {
            return instance.applicationContext
        }

        @SuppressLint("StaticFieldLeak")
        lateinit var app: WhatsWebApplication
    }

    fun getApp(): WhatsWebApplication {
        return app
    }

    fun getTopActivity(): Activity? {
        return topActivity
    }

    private var appOpenManager: AppOpenManager? = null

    override fun onCreate() {
        super.onCreate()
        FlurryAgent.Builder()
            .withLogEnabled(true)
            .build(this, "RGDK2PDFV4YDGZGPY2G8")
        FirebaseAnalyticsRecoder.getInstance(this);
        Logger.addLogAdapter(AndroidLogAdapter())

        MobileAds.initialize(this, OnInitializationCompleteListener{
            fun onInitializationComplete(initializationStatus: InitializationStatus?){

            }
        })
        appOpenManager = AppOpenManager(this);
        MMKV.initialize(applicationContext);
        initAppDefaultLanguage()

        instance = this
        app = this

        if (BuildConfig.DEBUG) {
            AppTracker.register(this)
        }

        initXLog()
        initKoin()
        initReport()
        initRemoteConfig()
        //FastSharePreferences初始化
        FastSharedPreferences.init(this)

        Handler().post(this::delayInit)
        initInterstitialAd()
    }

    fun initRemoteConfig() {
        var remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3 //3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    fun initAppDefaultLanguage() {
        var languageSimplified: String? =
            SpUtil.decodeString(SpUtil.Key.KEY_DEFAULT_LANGUAGE_SIMPLIFIED)
        var countrySimplified: String? =
            SpUtil.decodeString(SpUtil.Key.KEY_DEFAULT_COUNTRY_SIMPLIFIED)

        Logger.d(" initAppDefaultLanguage languageSimplified " + languageSimplified);
        if (!(languageSimplified?.isBlank()!!)) {
            LanguageUtil.updateLanguageConfiguration(
                applicationContext,
                Locale(languageSimplified, countrySimplified)
            )
        }
    }

    private fun initInterstitialAd() {
        interstitialAd.adUnitId = AdManager.getActivityStartInterstitialId()
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
        interstitialAd.loadAd(AdRequest.Builder().build())
    }

    private fun delayInit() {
        this.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {
                topActivity = activity
                if (!interstitialAd.isLoaded || activity.localClassName == AdActivity::class.java.name || isBackground) {
                    return
                }
                switchCount++
                val interval = AdRemoteConfig.getAdActivityStartInterval()
                if (interval != 0 && switchCount % interval == 0) {
                    interstitialAd.show()
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
                Log.e("zengxin", "11")
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityResumed(activity: Activity) {
            }
        })
    }

    private fun initXLog() {
        if (isMainProcess()) {
            XLog.init()
        }
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@WhatsWebApplication)
            modules(
                listOf(
                    mUtilsModules,
                    mViewModelModules,
                    mUseCaseModules,
                    mRepositoryModules,
                    mAppProviderModules,
                    mApiModules
                )
            )
        }
    }

    private fun isMainProcess(): Boolean {
        val pid = android.os.Process.myPid()
        val runningAppProcesses =
            (getSystemService(MultiDexApplication.ACTIVITY_SERVICE) as? ActivityManager)?.runningAppProcesses
                ?: emptyList()
        for (appProcess in runningAppProcesses) {
            if (appProcess.pid == pid) {
                return applicationInfo.packageName == appProcess.processName
            }
        }
        return false
    }

    private fun initReport() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        RemoteConfigManager.init(
            RemoteConfigManager.DefaultValueWrapper.Builder()
                .addBaseRemoteConfWrapper(AdRemoteConfig)
                .build()
        )
        RemoteConfigManager.asyncFetchConfigs()
    }
}
