package com.qltech.firebase.ad

import com.qltech.whatsweb.BuildConfig

object AdManager {

    fun getUserAgentReWardId(): String {
        return if (BuildConfig.DEBUG) AdConstant.Debug.REWARD_AD_ID else AdConstant.Release.REWARD_USERAGENT
    }

    fun getHomeAppOpenId(): String {
        return if (BuildConfig.DEBUG) AdConstant.Debug.APPOPEN_AD_ID else AdConstant.Release.APPOPEN_HOME
    }

    fun getWhatsWebBannerId(): String {
        return if (BuildConfig.DEBUG) AdConstant.Debug.BANNER_AD_ID else AdConstant.Release.BANNER_WHATSWEB
    }

    fun getUserAgentBannerId(): String {
        return if (BuildConfig.DEBUG) AdConstant.Debug.BANNER_AD_ID else AdConstant.Release.BANNER_USERAGENT
    }

    fun getRefreshPageInterstitialId(): String {
        return if (BuildConfig.DEBUG) AdConstant.Debug.INTERSTITIAL_AD_ID else AdConstant.Release.INTERSTITIAL_REFRESH_PAGE
    }

    fun getBackDialogNativeId(): String {
        return if (BuildConfig.DEBUG) AdConstant.Debug.NATIVE_AD_ID else AdConstant.Release.NATIVE_BACK_DIALOG
    }
}


