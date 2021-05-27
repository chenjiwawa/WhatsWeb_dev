package com.qltech.whatsweb.ui.callback

import android.app.Activity
import android.content.Intent
import com.qltech.whatsweb.ui.useragnet.UserAgentActivity
import com.qltech.whatsweb.util.FirebaseAnalyticsRecoder

class UnlockUserAgent(context: Activity?) :
    Unlock {
    private var context: Activity? = context;

    override fun onSuccess() {
        FirebaseAnalyticsRecoder.getInstance(context)
            .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_USERAGENT)
        context?.startActivity(Intent(context, UserAgentActivity::class.java))
    }

    override fun onFail() {

    }

}