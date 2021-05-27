package com.qltech.whatsweb.ui.useragnet

import android.os.Bundle
import com.orhanobut.logger.Logger
import com.qltech.whatsweb.R
import com.qltech.whatsweb.ui.base.BaseActivity
import com.qltech.whatsweb.ui.event.UserAgentEvent
import com.qltech.whatsweb.util.FirebaseAnalyticsRecoder
import kotlinx.android.synthetic.main.topbar_sub.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class UserAgentActivity:BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_useragent)

        titleinfo.setText(getString(R.string.useragent_title))
        back.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAnalyticsRecoder.getInstance(baseContext).recordScreen(UserAgentActivity::class.java.simpleName, FirebaseAnalyticsRecoder.Page.USERAGENT)
    }

    override fun isOpenEventbus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: UserAgentEvent?) {
        Logger.d(" onMessageEvent UserAgentEvent " + event?.userAgent)

        finish()

    }

}