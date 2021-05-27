package com.qltech.whatsweb.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.orhanobut.logger.Logger
import com.qltech.whatsweb.R
import com.qltech.whatsweb.ui.base.BaseActivity
import com.qltech.whatsweb.ui.event.LanguageEvent
import com.qltech.whatsweb.ui.setting.language.LanuageActivity
import com.qltech.whatsweb.util.Constants
import com.qltech.whatsweb.util.FirebaseAnalyticsRecoder
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.topbar_sub.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        titleinfo.setText(getString(R.string.settings))
        back.setOnClickListener { finish() }
        language.setOnClickListener {
            startActivity(Intent(baseContext, LanuageActivity::class.java))

            FirebaseAnalyticsRecoder.getInstance(this)
                .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_LANGUAGE)
        }
        about.setOnClickListener {
            FirebaseAnalyticsRecoder.getInstance(baseContext)
                .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_ABOUT)
            startActivity(Intent(this, AboutActivity::class.java)) }
        privacypolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.AppInfo.PRIVACY_POLICY)))

            FirebaseAnalyticsRecoder.getInstance(this)
                .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.OPEN_URL)
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAnalyticsRecoder.getInstance(baseContext).recordScreen(SettingActivity::class.java.simpleName, FirebaseAnalyticsRecoder.Page.SETTING)
    }

    override fun isOpenEventbus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LanguageEvent?) {
        Logger.d(" onMessageEvent ChangeLanguageEvent " + event?.language)

        finish()
    }

}