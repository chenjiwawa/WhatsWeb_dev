package com.qltech.firebase

import android.annotation.SuppressLint
import androidx.annotation.WorkerThread
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.qltech.whatsweb.BuildConfig
import com.qltech.messagesaver.common.executor.AppExecutors
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * @author Ryan lin
 */
object RemoteConfigManager {

    /**
     * cacheExpiration is set to 0 so each fetch will retrieve values from the server.
     * unit seconds
     */
    private val CACHE_EXPIRATION_SEC =
            if (BuildConfig.DEBUG) 0 else TimeUnit.HOURS.toSeconds(1)

    @SuppressLint("StaticFieldLeak")
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    private val mFetchListenerList = Collections.synchronizedList(ArrayList<IFetchListener>())

    interface IFetchListener {
        /**
         * on fetch completed
         */
        fun onFetchCompleted()
    }

    class DefaultValueWrapper private constructor(
            private val mRemoteConfigWrapperList: ArrayList<BaseRemoteConfigWrapper>
    ) {

        class Builder {
            private val remoteConfigWrapperList = ArrayList<BaseRemoteConfigWrapper>()
            fun addBaseRemoteConfWrapper(remoteConfigWrapper: BaseRemoteConfigWrapper) = apply {
                remoteConfigWrapperList.add(remoteConfigWrapper)
            }

            fun build() = DefaultValueWrapper(remoteConfigWrapperList)
        }

        internal fun getDefaultValues(): HashMap<String, Any> {
            val defaultValues: HashMap<String, Any> = HashMap()

            for (wrapper in mRemoteConfigWrapperList) {
                defaultValues.putAll(wrapper.getDefaultValues())
            }

            return defaultValues
        }
    }

    fun init(defaultValueWrapper: DefaultValueWrapper) {
        // Get Remote Config instance.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        // Create Remote Config Setting to enable developer mode.
        // Fetching configs from the server is normally limited to 5 requests per hour.
        // Enabling developer mode allows many more requests to be made per hour, so developers
        // can test different config values during development.
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(CACHE_EXPIRATION_SEC)
                .build()
        mFirebaseRemoteConfig?.setConfigSettingsAsync(configSettings)

        // Set default Remote Config values. In general you should have in app defaults for all
        // values that you may configure using Remote Config later on. The idea is that you
        // use the in app defaults and when you need to adjust those defaults, you set an updated
        // value in the App Manager console. Then the next time you application fetches from the
        // server, the updated value will be used. You can set defaults via an xml file like done
        // here or you can set defaults inline by using one of the other setDefaults methods.
        mFirebaseRemoteConfig?.setDefaultsAsync(defaultValueWrapper.getDefaultValues())
    }

    fun addFetchListener(listener: IFetchListener) {
        mFetchListenerList.add(listener)
    }

    fun removeFetchListener(listener: IFetchListener) {
        mFetchListenerList.remove(listener)
    }

    fun asyncFetchConfigs() {
        checkNotNull(mFirebaseRemoteConfig) { "Please call init first." }
        AppExecutors.getInstance().networkIO().execute { fetchConfigs() }
    }

    @WorkerThread
    private fun fetchConfigs() {
        checkNotNull(mFirebaseRemoteConfig) { "Please call init first." }
        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
        // fetched and cached config would be considered expired because it would have been fetched
        // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
        // throttling is in progress. The default expiration duration is 43200 (12 hours).
        mFirebaseRemoteConfig!!.fetchAndActivate().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }

            try {
                if (task.result == true) {
                    for (listener in mFetchListenerList) {
                        listener.onFetchCompleted()
                    }
                }
            } catch (e: Exception) {
                try {

                } catch (ignore: Exception) {
                }
            }
        }
    }

    fun getLong(key: String): Long {
        checkNotNull(mFirebaseRemoteConfig) { "Please call init first." }
        return mFirebaseRemoteConfig!!.getLong(key)
    }

    fun getString(key: String): String {
        checkNotNull(mFirebaseRemoteConfig) { "Please call init first." }
        return mFirebaseRemoteConfig!!.getString(key)
    }

    fun getBoolean(key: String): Boolean {
        checkNotNull(mFirebaseRemoteConfig) { "Please call init first." }
        return mFirebaseRemoteConfig!!.getBoolean(key)
    }

    fun getDouble(key: String): Double {
        checkNotNull(mFirebaseRemoteConfig) { "Please call init first." }
        return mFirebaseRemoteConfig!!.getDouble(key)
    }
}
