package com.qltech.common.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.qltech.common.helper.ISingleLiveObserver

/**
 * Extension for observing given [LiveData] on [Fragment.mViewLifecycleOwner]
 */
inline fun <reified T, LIVE : LiveData<T>> Fragment.subscribe(liveData: LIVE, noinline block: (T) -> Unit) {
    liveData.observerKt(viewLifecycleOwner, block)
}

inline fun <reified T, LIVE : ISingleLiveObserver<T>> Fragment.subscribe(liveData: LIVE, noinline block: (T) -> Unit) {
    liveData.observerKt(viewLifecycleOwner, block)
}