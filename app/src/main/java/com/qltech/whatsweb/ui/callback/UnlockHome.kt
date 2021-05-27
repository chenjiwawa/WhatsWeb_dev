package com.qltech.whatsweb.ui.callback

import android.app.Activity
import com.orhanobut.logger.Logger
import com.qltech.whatsweb.ui.callback.Unlock

class UnlockHome(context: Activity?) : Unlock {
    private var context: Activity? = context;

    override fun onSuccess() {

    }

    override fun onFail() {
        Logger.d(" UnlockApp onFail ");
        context?.finish()
    }

}