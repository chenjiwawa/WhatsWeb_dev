package com.qltech.messagesaver.common.utils.preference

import androidx.annotation.StringDef
import com.qltech.WhatsWebApplication

object CommonPreferenceWrapper {

    @StringDef
    private annotation class Key {
        companion object {
            const val TUTORIAL_ICON_SHOW_COUNT = "tutorial_icon_show_count"
        }
    }

    var tutorialIconShowCount: Int
        get() = SharedPreferencesUtils.getInt(WhatsWebApplication.getAppContext(),
            Key.TUTORIAL_ICON_SHOW_COUNT, 0)
        set(type) = SharedPreferencesUtils.setInt(WhatsWebApplication.getAppContext(),
            Key.TUTORIAL_ICON_SHOW_COUNT, type)
}
