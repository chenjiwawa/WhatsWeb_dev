package com.qltech.common.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qltech.common.helper.ISingleLiveObserver

// /////////////////////////////////////////////////////////////////////////
// Architecture component
// /////////////////////////////////////////////////////////////////////////
inline fun <reified T> LiveData<T>.observerKt(owner: LifecycleOwner, crossinline block: (T) -> Unit) {
    observe(owner, { block(it) })
}

inline fun <reified T> ISingleLiveObserver<T>.observerKt(owner: LifecycleOwner, crossinline block: (T) -> Unit) {
    observeSingle(owner, { block(it) })
}

fun <T> MutableLiveData<T>.refresh() {
    value = value
}