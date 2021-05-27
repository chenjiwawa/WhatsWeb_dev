package com.qltech.whatsweb.ui.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import com.qltech.common.utils.IntentUtils
import com.qltech.whatsweb.util.LanguageUtil
import com.qltech.whatsweb.util.SpUtil
import org.greenrobot.eventbus.EventBus
import java.util.*

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isOpenEventbus()) {
            EventBus.getDefault().register(this)
        }
        initDefaultLanguage()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isOpenEventbus()) {
            EventBus.getDefault().unregister(this)
        }
    }


    fun initDefaultLanguage() {
        var languageSimplified: String? =
            SpUtil.decodeString(SpUtil.Key.KEY_DEFAULT_LANGUAGE_SIMPLIFIED)
        var countrySimplified: String? =
            SpUtil.decodeString(SpUtil.Key.KEY_DEFAULT_COUNTRY_SIMPLIFIED)

        Logger.d(" initAppDefaultLanguage languageSimplified " + languageSimplified);
        if (!(languageSimplified?.isBlank()!!)) {
            LanguageUtil.updateLanguageConfiguration(
                this,
                Locale(languageSimplified, countrySimplified)
            )
        }
    }

    open fun isOpenEventbus(): Boolean {
        return false
    }

}