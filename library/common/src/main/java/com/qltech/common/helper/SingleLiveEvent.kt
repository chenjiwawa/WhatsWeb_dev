package com.qltech.common.helper

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 只会触发一次(一定会触发)
 */
class SingleLiveEvent<T> : MutableLiveData<T>(), ISingleLiveObserver<T> {

    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observeSingle(owner: LifecycleOwner, observer: Observer<T>) =
        observe(owner, observer)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }

        // Observe the internal MutableLiveData
        super.observe(owner, { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(@Nullable t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    companion object {
        private const val TAG = "SingleLiveEvent"
    }
}

/**
 * 所有当前的监听者各会收到一次(当前没人监听则无人会收到)
 */
class OneShotLiveEvent<T> : MutableLiveData<T>(), ISingleLiveObserver<T> {

    private val pendingMap: MutableMap<Int, AtomicBoolean> = HashMap()

    @MainThread
    override fun observeSingle(owner: LifecycleOwner, observer: Observer<T>) =
        observe(owner, observer)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        val key = observer.hashCode()
        pendingMap[key] = AtomicBoolean(false)

        // Observe the internal MutableLiveData
        super.observe(owner, Observer { t ->
            val pending = pendingMap[key] ?: return@Observer

            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        pendingMap.forEach {
            it.value.set(true)
        }
        super.setValue(t)
    }

}

interface ISingleLiveObserver<T> {
    fun observeSingle(owner: LifecycleOwner, observer: Observer<T>)
}