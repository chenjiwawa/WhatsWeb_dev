package com.qltech.whatsweb.util

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event
import com.google.firebase.analytics.FirebaseAnalytics.Param
import com.google.firebase.analytics.ktx.logEvent
import com.orhanobut.logger.Logger

class FirebaseAnalyticsRecoder private constructor(context: Context) {

    object FirebaseAnalyticsKey {
        const val SHOW_WHATSWEB = "show_whatsweb"
        const val SHOW_STATUSSAVER = "show_statussaver"
        const val SHOW_LANGUAGE = "show_language"
        const val SHOW_PRIVACYPOLICY = "show_privacypolicy"
        const val SHOW_FULLSCREEN = "show_fullscreen"
        const val SHOW_USERAGENT = "show_useragent"
        const val SELECT_USERAGENT_ITEM = "select_useragent_item"
        const val USERAGENT_OS = "useragent_os"
        const val SHOW_ABOUT = "show_about"
        const val SHOW_SETTING = "show_setting"
        const val SHOW_INTROINFO = "show_introInfo"
        const val SHOW_POPUPDIALOG = "show_popupdialog"
        const val SHOW_SNACKBAR = "show_snackbar"
        const val OPEN_URL = "open_url"
        const val SHOW_TOAST = "show_toast"
        const val TOGGLE_KEYBOARD = "toggle_keyboard"
        const val LOAD_WHATSAPP = "load_whatsapp"
        const val RELOAD_WHATSAPP = "reload_whatsapp"
        //激励广告网络异常导致加载失败
        const val REWARD_AD_LOAD_INTERNET_ERROR = "reward_ad_load_internet_error"
        //激励广告其他异常导致加载失败
        const val REWARD_AD_LOAD_OTHER_ERROR = "reward_ad_load_other_error"

    }

    object Page {
        const val MAIN = "Main"
        const val WHATSWEB = "WhatsWeb"
        const val STATUSSAVER = "StatusSaver"
        const val ABOUT = "About"
        const val USERAGENT = "UserAgent"
        const val SETTING = "Setting"
        const val CHANGELANUAGE = "ChangeLanuage"

    }


    var mContext: Context = context;
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    init {
        Logger.d("mContext " + mContext)
        Logger.d("context " + context)

        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    companion object {
        @Volatile
        var instance: FirebaseAnalyticsRecoder? = null

        fun getInstance(context: Context?): FirebaseAnalyticsRecoder {
            if (instance == null) {
                synchronized(FirebaseAnalyticsRecoder::class) {
                    if (instance == null) {
                        instance = context?.let { FirebaseAnalyticsRecoder(it) }
                    }
                }
            }
            return instance!!
        }
    }

    fun recordScreen(screanClass: String, screenName: String) {
        Logger.d("recordScreen " + screanClass + " " + screenName)

        firebaseAnalytics.logEvent(Event.SCREEN_VIEW) {
            param(Param.SCREEN_NAME, screenName)
            param(Param.SCREEN_CLASS, screanClass)
        }
    }

    fun record(eventName: String) {
        Logger.d("logEvent " + eventName)

        firebaseAnalytics.logEvent(eventName){

        };
    }

    fun record(eventName: String,key1: String, value1: String) {
        Logger.d("logEvent " + eventName +" " + key1 + " " + value1)

        firebaseAnalytics.logEvent(eventName) {
            param(key1, value1)
        }
    }


    fun record(eventName: String,key1: String, value1: String,key2: String, value2: String) {
        Logger.d("logEvent " + eventName +" " + key1 + " " + value1+" " + key2 + " " + value2)

        firebaseAnalytics.logEvent(eventName) {
            param(key1, value1)
            param(key2, value2)
        }
    }

}
