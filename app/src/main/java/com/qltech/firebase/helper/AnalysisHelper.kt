package com.qltech.firebase.helper

import android.content.Context
import com.qltech.common.utils.XLog
import com.qltech.ui.helper.AnalysisEvent
import com.qltech.ui.helper.IAnalysisHelper

object AnalysisHelper : IAnalysisHelper {

    private const val TAG = "AnalysisHelper"

    override fun onPageBegin(context: Context, pageName: String) {
        XLog.d(TAG, "[onPageBegin] $pageName")
    }

    override fun onPageEnd(context: Context, pageName: String) {
        XLog.d(TAG, "[onPageEnd] $pageName")
    }

    override fun send(context: Context, bean: AnalysisEvent) {
        XLog.d(TAG, "[event] $bean")
    }

}
