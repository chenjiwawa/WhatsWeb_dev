package com.qltech.firebase.ad.saver

import com.qltech.firebase.ad.AdConstant
import com.qltech.whatsweb.BuildConfig
import com.qltech.firebase.remoteconfig.AdRemoteConfig

object AdManager {

    fun getMessageSaverBannerId(): String {
        return if (BuildConfig.DEBUG) AdConstant.Debug.BANNER_AD_ID else AdConstant.Release.BANNER_MESSAGESAVER
    }

    fun getMessageDetailBannerId(): String {
        return if (BuildConfig.DEBUG) AdConstant.Debug.BANNER_AD_ID else AdConstant.Release.BANNER_MESSAGEDETAIL
    }

    fun getMessageListId(): String {
        return if (BuildConfig.DEBUG) AdConstant.Debug.NATIVE_AD_ID else AdConstant.Release.NATIVE_MESSAGELIST
    }

    fun getBackDialogNativeId(): String {
        return if (BuildConfig.DEBUG) AdConstant.Debug.NATIVE_AD_ID else AdConstant.Release.NATIVE_BACK_DIALOG
    }

    fun getOpenId(): String {
        return if (BuildConfig.DEBUG) AdConstant.Debug.APPOPEN_AD_ID else AdConstant.Release.APPOPEN_HOME
    }

    fun getActivityStartInterstitialId(): String {
        return if (BuildConfig.DEBUG) AdConstant.Debug.INTERSTITIAL_AD_ID else AdConstant.Release.INTERSTITIAL_REFRESH_PAGE
    }

    fun getPageSwitchInterstitialId(): String {
        return if (BuildConfig.DEBUG) AdConstant.Debug.INTERSTITIAL_AD_ID else AdConstant.Release.INTERSTITIAL_REFRESH_PAGE
    }

}


