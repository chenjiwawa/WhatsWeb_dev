package com.qltech.whatsweb.ui.setting.language

import android.os.Bundle
import com.orhanobut.logger.Logger
import com.qltech.whatsweb.R
import com.qltech.whatsweb.ui.base.BaseActivity
import com.qltech.whatsweb.ui.event.LanguageEvent
import com.qltech.whatsweb.util.FirebaseAnalyticsRecoder
import kotlinx.android.synthetic.main.topbar_sub.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LanuageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        titleinfo.setText(getString(R.string.menu_language))
        back.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAnalyticsRecoder.getInstance(baseContext).recordScreen(
            LanuageActivity::class.java.simpleName,
            FirebaseAnalyticsRecoder.Page.CHANGELANUAGE
        )
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