package com.qltech.common.extensions

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.qltech.common.helper.ISingleLiveObserver

/**
 * Extension for observing given [LiveData] on [Fragment.mViewLifecycleOwner]
 */
inline fun <reified T, LIVE : LiveData<T>> ComponentActivity.subscribe(liveData: LIVE, noinline block: (T) -> Unit) {
    liveData.observerKt(this, block)
}

inline fun <reified T, LIVE : ISingleLiveObserver<T>> ComponentActivity.subscribe(liveData: LIVE, noinline block: (T) -> Unit) {
    liveData.observerKt(this, block)
}