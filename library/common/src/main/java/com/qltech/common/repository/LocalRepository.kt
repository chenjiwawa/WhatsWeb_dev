package com.qltech.common.repository

import androidx.core.content.edit
import com.google.gson.Gson
import com.qltech.common.constant.SharedPreferenceConstant
import com.qltech.common.utils.XLog
import com.qltech.base.helper.AppHelper
import com.qltech.base.helper.ISharedPreferences

interface LocalRepositoryImpl<T> {
    fun getCacheData(): T
    fun setCacheData(data: T)
}

class LocalRepository<T>(
    private val dataClass: Class<T>,
    sharedPreferencesProvider: ISharedPreferences = AppHelper.sharedPreferencesHelper
) : LocalRepositoryImpl<T> {

    companion object {
        private const val TAG = "CacheModel"
    }

    private val className = dataClass.name
    private val gson: Gson = Gson()
    private val sharedPreferences = sharedPreferencesProvider.getSharedPreferences()

    override fun getCacheData(): T {
        val beanString = sharedPreferences.getString(SharedPreferenceConstant.KEY_CACHE_DATA + className, "{}")
        return gson.fromJson(beanString, dataClass)
    }

    override fun setCacheData(data: T) {
        XLog.d(TAG, "[setCacheData] data: $data")
        val beanString = gson.toJson(data)
        sharedPreferences.edit {
            putString(SharedPreferenceConstant.KEY_CACHE_DATA + className, beanString)
        }
    }

}