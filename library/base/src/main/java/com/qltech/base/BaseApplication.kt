package com.qltech.base

import androidx.multidex.MultiDexApplication

abstract class BaseApplication : MultiDexApplication() {

    companion object {
        private lateinit var instance: BaseApplication

        internal fun getInstance(): BaseApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}