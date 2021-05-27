package com.qltech.whatsweb.ui.setting

import android.os.Bundle
import com.qltech.whatsweb.R
import com.qltech.whatsweb.ui.base.BaseActivity
import com.qltech.whatsweb.util.FirebaseAnalyticsRecoder
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.topbar_sub.*

class AboutActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        titleinfo.setText(getString(R.string.menu_about))
        about.setText(String.format(getString(R.string.about), getString(R.string.versionName)))
        back.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAnalyticsRecoder.getInstance(baseContext).recordScreen(AboutActivity::class.java.simpleName, FirebaseAnalyticsRecoder.Page.ABOUT)
    }


}