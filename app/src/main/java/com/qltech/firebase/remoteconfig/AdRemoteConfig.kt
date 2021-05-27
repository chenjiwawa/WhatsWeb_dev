package com.qltech.firebase.remoteconfig

import com.flurry.sdk.t
import com.qltech.firebase.BaseRemoteConfigWrapper
import com.qltech.firebase.RemoteConfigManager
import java.util.*

object AdRemoteConfig : BaseRemoteConfigWrapper() {
    private const val TAG = "AdRemoteConfig"
    private val adConfigMap: HashMap<String, String> = HashMap()

    init {
        RemoteConfigManager.addFetchListener(object : RemoteConfigManager.IFetchListener {
            override fun onFetchCompleted() {
                prepareAdConfigs()
            }
        })
    }

    override fun getDefaultValues(): HashMap<String, Any> {
        val defaults = HashMap<String, Any>()

        defaults[RemoteConfigKey.AD_MASTER_SWITCH] = true
        defaults[RemoteConfigKey.AD_MESSAGE_GRID_ENABLE] = false
        defaults[RemoteConfigKey.AD_MESSAGE_GRID_START_INDEX] = 0
        defaults[RemoteConfigKey.AD_MESSAGE_GRID_INTERVAL_INDEX] = 2
        defaults[RemoteConfigKey.AD_MESSAGE_GRID_SIZE_MIN_LIMIT] = 0
        defaults[RemoteConfigKey.AD_OPEN_SHOW_INTERVAL_INDEX] = 0
        defaults[RemoteConfigKey.AD_ADMOB_ACTIVITY_START_INTERVAL] = 3
        defaults[RemoteConfigKey.AD_ADMOB_SWITCH_PAGE_INTERVAL] = 3

        return defaults
    }

    private fun prepareAdConfigs() {
        adConfigMap.clear()
        adConfigMap[RemoteConfigKey.AD_MASTER_SWITCH] =
            RemoteConfigManager.getBoolean(RemoteConfigKey.AD_MASTER_SWITCH).toString()
        adConfigMap[RemoteConfigKey.AD_MESSAGE_GRID_ENABLE] =
            RemoteConfigManager.getBoolean(RemoteConfigKey.AD_MESSAGE_GRID_ENABLE).toString()
        adConfigMap[RemoteConfigKey.AD_MESSAGE_GRID_START_INDEX] =
            RemoteConfigManager.getLong(RemoteConfigKey.AD_MESSAGE_GRID_START_INDEX).toInt()
                .toString()
        adConfigMap[RemoteConfigKey.AD_MESSAGE_GRID_INTERVAL_INDEX] =
            RemoteConfigManager.getLong(RemoteConfigKey.AD_MESSAGE_GRID_INTERVAL_INDEX).toInt()
                .toString()
        adConfigMap[RemoteConfigKey.AD_MESSAGE_GRID_SIZE_MIN_LIMIT] =
            RemoteConfigManager.getLong(RemoteConfigKey.AD_MESSAGE_GRID_SIZE_MIN_LIMIT).toInt()
                .toString()
        adConfigMap[RemoteConfigKey.AD_OPEN_SHOW_INTERVAL_INDEX] =
            RemoteConfigManager.getLong(RemoteConfigKey.AD_OPEN_SHOW_INTERVAL_INDEX).toInt()
                .toString()
        adConfigMap[RemoteConfigKey.AD_ADMOB_ACTIVITY_START_INTERVAL] =
            RemoteConfigManager.getLong(RemoteConfigKey.AD_ADMOB_ACTIVITY_START_INTERVAL).toInt()
                .toString()
        adConfigMap[RemoteConfigKey.AD_ADMOB_SWITCH_PAGE_INTERVAL] =
            RemoteConfigManager.getLong(RemoteConfigKey.AD_ADMOB_SWITCH_PAGE_INTERVAL).toInt()
                .toString()
    }

    private fun checkConfigMap() {
        if (adConfigMap.isEmpty()) {
            prepareAdConfigs()
        }
    }

    fun getAdSwitch(): Boolean {
        checkConfigMap()
        return adConfigMap[RemoteConfigKey.AD_MASTER_SWITCH]?.toBoolean() ?: true
    }

    fun getAdStatusGridEnable(): Boolean {
        checkConfigMap()
        return adConfigMap[RemoteConfigKey.AD_MESSAGE_GRID_ENABLE]?.toBoolean() ?: true
    }

    fun getAdStatusGridStartIndex(): Int {
        checkConfigMap()
        return adConfigMap[RemoteConfigKey.AD_MESSAGE_GRID_START_INDEX]?.toInt() ?: 5
    }

    fun getAdStatusGridIntervalIndex(): Int {
        checkConfigMap()
        return adConfigMap[RemoteConfigKey.AD_MESSAGE_GRID_INTERVAL_INDEX]?.toInt() ?: 10
    }

    fun getAdStatusGridSizeMinLimit(): Int {
        checkConfigMap()
        return adConfigMap[RemoteConfigKey.AD_MESSAGE_GRID_SIZE_MIN_LIMIT]?.toInt() ?: 0
    }

    fun getAdOpenShowIntervalIndex(): Int {
        checkConfigMap()
        return adConfigMap[RemoteConfigKey.AD_OPEN_SHOW_INTERVAL_INDEX]?.toInt() ?: 0
    }

    fun getAdActivityStartInterval(): Int {
        checkConfigMap()
        return adConfigMap[RemoteConfigKey.AD_ADMOB_ACTIVITY_START_INTERVAL]?.toInt() ?: 0
    }

    fun getAdSwitchPageInterval(): Int {
        checkConfigMap()
        return adConfigMap[RemoteConfigKey.AD_ADMOB_SWITCH_PAGE_INTERVAL]?.toInt() ?: 0
    }
}
