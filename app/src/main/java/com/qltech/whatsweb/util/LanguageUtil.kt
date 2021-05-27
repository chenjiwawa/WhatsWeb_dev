package com.qltech.whatsweb.util

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import com.orhanobut.logger.Logger
import java.util.*

class LanguageUtil {

    object Language {
        const val en = "英语"
        const val es = "西班牙语"
        const val pt = "葡萄牙语"
        const val id = "印尼语"
    }

    object LanguageSimplified {
        const val en = "en"
        const val es = "es"
        const val pt = "pt"
        const val id = "in"
    }

    object CountrySimplified {
        const val en = "us"
        const val es = "es"
        const val pt = "pt"
        const val id = "id"
    }

    companion object {
        fun getSystemDefaultLocale(): Locale? {
            //7.0以下直接获取系统默认语言
            if (Build.VERSION.SDK_INT < 24) {
                return Locale.getDefault()
            }

            // 7.0以上获取系统首选语言
            return LocaleList.getDefault()[0]
        }

        fun updateLanguageConfiguration(mContext: Context, mLocale: Locale) {
            Logger.d(" updateLanguageConfiguration mContext " + mContext+" mLocale " + mLocale)

            val mResources: Resources = mContext.getResources()
            val mDisplayMetrics = mResources.displayMetrics
            val mConfiguration = mResources.configuration
            mConfiguration.locale = mLocale
            mResources.updateConfiguration(mConfiguration, mDisplayMetrics)
        }

    }
}