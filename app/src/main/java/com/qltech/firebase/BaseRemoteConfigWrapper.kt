package com.qltech.firebase

import java.util.*

abstract class BaseRemoteConfigWrapper {
    abstract fun getDefaultValues(): HashMap<String, Any>
}
