package com.qltech.whatsweb.ui

import android.os.Bundle
import android.view.View
import com.orhanobut.logger.Logger
import com.qltech.whatsweb.R
import com.qltech.whatsweb.ui.base.BaseActivity
import com.qltech.whatsweb.ui.event.FullScreenEvent
import com.qltech.whatsweb.util.FirebaseAnalyticsRecoder
import kotlinx.android.synthetic.main.activity_whatsweb.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class WhatsWebActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whatsweb)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setHomeButtonEnabled(true);

    }

    override fun onResume() {
        super.onResume()
        FirebaseAnalyticsRecoder.getInstance(baseContext)
            .recordScreen(WhatsWebActivity::class.java.simpleName, FirebaseAnalyticsRecoder.Page.WHATSWEB)
    }

    override fun isOpenEventbus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: FullScreenEvent?) {
        Logger.d(" onMessageEvent FullScreenEvent " + event?.isFullScreen)

        if (event?.isFullScreen!!) {
            appbar.visibility = View.VISIBLE
        } else {
            appbar.visibility = View.GONE
        }
    }

}
