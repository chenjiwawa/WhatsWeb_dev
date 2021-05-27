package com.qltech.firebase.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.qltech.WhatsWebApplication

class FirebaseAnalyticHelper private constructor() {

    companion object {
        private var instance: FirebaseAnalytics? = null
            get() {
                if (field == null) {
                    field = FirebaseAnalytics.getInstance(WhatsWebApplication.getAppContext())
                }
                return field
            }
        fun get(): FirebaseAnalytics{
            return instance!!
        }

        fun logEvent(event:String){
            get().logEvent(event, Bundle())
        }
    }
}