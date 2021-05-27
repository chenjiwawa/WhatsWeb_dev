package com.qltech.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.qltech.common.utils.XLog
import com.qltech.ui.helper.IAnalysisHelper
import org.koin.android.ext.android.inject
import kotlin.system.measureTimeMillis

abstract class BaseDialogFragment(@LayoutRes val layoutResId: Int) : DialogFragment() {

    private val analysisHelper: IAnalysisHelper by inject()
    protected open val isTrackPageEnable: Boolean = false
    protected open val screenType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View?
        val cost = measureTimeMillis {
            view = inflater.inflate(layoutResId, container, false)
        }
        XLog.d(TAG, "[onCreateView] cost time $cost when inflate $view")

        return view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        //TalkingData
        if (isVisibleToUser) {
            onTrackPageStart()
        } else {
            onTrackPageEnd()
        }
    }

    override fun onResume() {
        super.onResume()
        onTrackPageStart()
    }

    override fun onPause() {
        super.onPause()
        onTrackPageEnd()
    }


    private fun onTrackPageStart() {
        if (!isResumed || !userVisibleHint) return
        val context = context ?: return

        if (isTrackPageEnable) {
            analysisHelper.onPageBegin(context, getScreenName())
        }
    }

    private fun onTrackPageEnd() {
        if (!isResumed && !userVisibleHint) return
        val context = context ?: return

        if (isTrackPageEnable) {
            analysisHelper.onPageEnd(context, getScreenName())
        }
    }

    private fun getScreenName(): String {
        val screenName = javaClass.simpleName
        return if (TextUtils.isEmpty(screenType)) {
            screenName
        } else {
            "$screenName-$screenType"
        }
    }

    companion object {
        val TAG: String = BaseDialogFragment::class.java.simpleName
    }
}