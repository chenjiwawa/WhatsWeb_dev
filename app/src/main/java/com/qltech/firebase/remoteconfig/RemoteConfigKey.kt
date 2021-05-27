package com.qltech.firebase.remoteconfig

import androidx.annotation.StringDef

/**
 * @author SkeeterWang Created on 2019/4/18.
 */
@StringDef
annotation class RemoteConfigKey {
    companion object {

        const val AD_MASTER_SWITCH = "ad_master_switch"

        const val AD_MESSAGE_GRID_ENABLE = "ad_status_grid_enable"

        const val AD_MESSAGE_GRID_START_INDEX = "ad_status_grid_start_index"

        const val AD_MESSAGE_GRID_INTERVAL_INDEX = "ad_status_grid_interval_index"

        const val AD_MESSAGE_GRID_SIZE_MIN_LIMIT = "ad_status_grid_size_min_limit"

        const val AD_OPEN_SHOW_INTERVAL_INDEX = "ad_open_show_interval_index"

        const val AD_ADMOB_NATIVE_ID = "ad_admob_native_id"

        const val AD_ADMOB_ACTIVITY_START_INTERVAL = "ad_admob_activity_start_interval"

        const val AD_ADMOB_SWITCH_PAGE_INTERVAL = "ad_admob_switch_page_interval"
    }
}
