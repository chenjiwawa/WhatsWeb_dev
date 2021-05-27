package com.qltech.messagesaver.common.utils

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.text.TextUtils

object AppUtils {
    /**
     * get App versionCode
     * @param context
     * @return
     */
    @JvmStatic
    fun getVersionCode(context: Context): String {
        val packageManager = context.packageManager
        val packageInfo: PackageInfo
        var versionCode = ""
        try {
            packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionCode = packageInfo.versionCode.toString() + ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionCode
    }

    /**
     * get App versionName
     * @param context
     * @return
     */
    @JvmStatic
    fun getVersionName(context: Context): String {
        val packageManager = context.packageManager
        val packageInfo: PackageInfo
        var versionName = ""
        try {
            packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }

    /**
     * 是否是app进程
     */
    fun isAppProcess(context: Context): Boolean {
        val process = getProcessName(context)
        return !TextUtils.isEmpty(process) && process!!.equals(context.packageName, ignoreCase = true)
    }

    /**
     * 得到当前进程名
     */
    fun getProcessName(context: Context): String? {
        val pid = android.os.Process.myPid()
        val mActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in mActivityManager.runningAppProcesses) {
            if (appProcess.pid == pid) {
                return appProcess.processName
            }
        }
        return null
    }
}