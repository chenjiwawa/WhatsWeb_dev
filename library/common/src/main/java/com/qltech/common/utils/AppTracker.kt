package com.qltech.common.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

object AppTracker : FragmentManager.FragmentLifecycleCallbacks(),
    Application.ActivityLifecycleCallbacks {
    private const val TAG = "AppTracker"
    private const val TAG_ACTIVITY = TAG + "_activity"
    private const val TAG_FRAGMENT = TAG + "_fragment"

    fun register(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun unRegister(application: Application) {
        application.unregisterActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        XLog.i(TAG_ACTIVITY, "[onActivityCreated] ${activity.javaClass.simpleName}")
        (activity as? FragmentActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(
            this,
            true
        )
    }

    override fun onActivityStarted(activity: Activity) {
        XLog.v(TAG_ACTIVITY, "[onActivityStarted] ${activity.javaClass.simpleName}")
    }

    override fun onActivityResumed(activity: Activity) {
        XLog.v(TAG_ACTIVITY, "[onActivityResumed] ${activity.javaClass.simpleName}")
    }

    override fun onActivityPaused(activity: Activity) {
        XLog.v(TAG_ACTIVITY, "[onActivityPaused] ${activity.javaClass.simpleName}")
    }

    override fun onActivityStopped(activity: Activity) {
        XLog.v(TAG_ACTIVITY, "[onActivityStopped] ${activity.javaClass.simpleName}")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        XLog.v(TAG_ACTIVITY, "[onActivitySaveInstanceState] ${activity.javaClass.simpleName}")
    }

    override fun onActivityDestroyed(activity: Activity) {
        XLog.i(TAG_ACTIVITY, "[onActivityDestroyed] ${activity.javaClass.simpleName}")

        (activity as? FragmentActivity)?.supportFragmentManager?.unregisterFragmentLifecycleCallbacks(
            this
        )
    }

    override fun onFragmentCreated(
        fm: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        XLog.i(TAG_FRAGMENT, "[onFragmentCreated] ${fragment.javaClass.simpleName}")
    }

    override fun onFragmentActivityCreated(
        fm: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        XLog.v(TAG_FRAGMENT, "[onFragmentActivityCreated] ${fragment.javaClass.simpleName}")
    }

    override fun onFragmentViewCreated(
        fm: FragmentManager,
        fragment: Fragment,
        v: View,
        savedInstanceState: Bundle?
    ) {
        XLog.v(TAG_FRAGMENT, "[onFragmentViewCreated] ${fragment.javaClass.simpleName}")
    }

    override fun onFragmentAttached(fm: FragmentManager, fragment: Fragment, context: Context) {
        XLog.v(TAG_FRAGMENT, "[onFragmentAttached] ${fragment.javaClass.simpleName}")
    }

    override fun onFragmentStarted(fm: FragmentManager, fragment: Fragment) {
        XLog.v(TAG_FRAGMENT, "[onFragmentStarted] ${fragment.javaClass.simpleName}")
    }

    override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) {
        XLog.v(TAG_FRAGMENT, "[onFragmentResumed] ${fragment.javaClass.simpleName}")
    }

    override fun onFragmentPaused(fm: FragmentManager, fragment: Fragment) {
        XLog.v(TAG_FRAGMENT, "[onFragmentPaused] ${fragment.javaClass.simpleName}")
    }

    override fun onFragmentStopped(fm: FragmentManager, fragment: Fragment) {
        XLog.v(TAG_FRAGMENT, "[onFragmentStopped] ${fragment.javaClass.simpleName}")
    }

    override fun onFragmentDetached(fm: FragmentManager, fragment: Fragment) {
        XLog.v(TAG_FRAGMENT, "[onFragmentDetached] ${fragment.javaClass.simpleName}")
    }

    override fun onFragmentSaveInstanceState(
        fm: FragmentManager,
        fragment: Fragment,
        outState: Bundle
    ) {
        XLog.v(TAG_FRAGMENT, "[onFragmentSaveInstanceState] ${fragment.javaClass.simpleName}")
    }


    override fun onFragmentViewDestroyed(fm: FragmentManager, fragment: Fragment) {
        XLog.v(TAG_FRAGMENT, "[onFragmentViewDestroyed] ${fragment.javaClass.simpleName}")
    }

    override fun onFragmentDestroyed(fm: FragmentManager, fragment: Fragment) {
        XLog.i(TAG_FRAGMENT, "[onFragmentDestroyed] ${fragment.javaClass.simpleName}")
    }
}
