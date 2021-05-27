package com.qltech.messagesaver.common.utils.preference

import android.content.Context
import android.preference.PreferenceManager
import java.util.*

/**
 * Created by yy on 2014/9/28.
 */
object SharedPreferencesUtils {
    private const val SHARED_PREFERENCES = "SHAREED_PREF"
    private val sLock = Any()
    private val sSharedPreferenceMap: MutableMap<String?, Any?> = HashMap()
    fun setBoolean(context: Context, str: String?, b: Boolean) {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (`object` is Boolean) {
                    if (`object` == b) {
                        return
                    }
                }
            }
        }
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            synchronized(sLock) { sSharedPreferenceMap.put(str, b) }
            val editor = sharedPreferences.edit()
            editor.putBoolean(str, b)
            editor.apply()
        }
    }

    fun getBoolean(context: Context, str: String?, def: Boolean): Boolean {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (`object` is Boolean) {
                    return `object`
                }
            }
        }
        var sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null && sharedPreferences.contains(str)) {
            return sharedPreferences.getBoolean(str, def)
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences?.getBoolean(str, def) ?: def
    }

    fun getBoolean(context: Context, str: String?): Boolean {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (`object` is Boolean) {
                    return `object`
                }
            }
        }
        var sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null && sharedPreferences.contains(str)) {
            return sharedPreferences.getBoolean(str, false)
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences?.getBoolean(str, false) ?: false
    }

    fun setInt(context: Context, str: String?, i: Int) {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (`object` is Int) {
                    if (`object` == i) {
                        return
                    }
                }
            }
        }
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            synchronized(sLock) { sSharedPreferenceMap.put(str, i) }
            val editor = sharedPreferences.edit()
            editor.putInt(str, i)
            editor.apply()
        }
    }

    fun getInt(context: Context, str: String?, def: Int): Int {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (`object` is Int) {
                    return `object`
                }
            }
        }
        var sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null && sharedPreferences.contains(str)) {
            return sharedPreferences.getInt(str, def)
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences?.getInt(str, def) ?: def
    }

    fun getInt(context: Context, str: String?): Int {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (`object` is Int) {
                    return `object`
                }
            }
        }
        var sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null && sharedPreferences.contains(str)) {
            return sharedPreferences.getInt(str, 0)
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences?.getInt(str, 0) ?: -1
    }

    fun setLong(context: Context, str: String?, l: Long) {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (`object` is Long) {
                    if (`object` == l) {
                        return
                    }
                }
            }
        }
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            synchronized(sLock) { sSharedPreferenceMap.put(str, l) }
            val editor = sharedPreferences.edit()
            editor.putLong(str, l)
            editor.apply()
        }
    }

    fun getLong(context: Context, str: String?, def: Long): Long {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (`object` is Long) {
                    return `object`
                }
            }
        }
        var sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null && sharedPreferences.contains(str)) {
            return sharedPreferences.getLong(str, def)
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences?.getLong(str, def) ?: def
    }

    fun getLong(context: Context, str: String?): Long {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (`object` is Long) {
                    return `object`
                }
            }
        }
        var sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null && sharedPreferences.contains(str)) {
            return sharedPreferences.getLong(str, 0)
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences?.getLong(str, 0) ?: -1
    }

    fun setFloat(context: Context, str: String?, f: Float) {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (`object` is Float) {
                    if (java.lang.Float.compare(`object`, f) == 0) {
                        return
                    }
                }
            }
        }
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            synchronized(sLock) { sSharedPreferenceMap.put(str, f) }
            val editor = sharedPreferences.edit()
            editor.putFloat(str, f)
            editor.apply()
        }
    }

    fun getFloat(context: Context, str: String?, def: Float): Float {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (`object` is Float) {
                    return `object`
                }
            }
        }
        var sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null && sharedPreferences.contains(str)) {
            return sharedPreferences.getFloat(str, def)
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences?.getFloat(str, def) ?: def
    }

    fun getFloat(context: Context, str: String?): Float {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (`object` is Float) {
                    return `object`
                }
            }
        }
        var sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null && sharedPreferences.contains(str)) {
            return sharedPreferences.getFloat(str, 0f)
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences?.getFloat(str, 0f) ?: -1f
    }

    fun setString(context: Context, str: String?, s: String?) {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (`object` is String && `object` == s
                    || `object` == null && s == null) {
                    return
                }
            }
        }
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            synchronized(sLock) { sSharedPreferenceMap.put(str, s) }
            val editor = sharedPreferences.edit()
            editor.putString(str, s)
            editor.apply()
        }
    }

    fun getString(context: Context, str: String?, def: String?): String? {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (str == null) {
                    return null
                } else if (`object` is String) {
                    return `object`
                }
            }
        }
        var sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null && sharedPreferences.contains(str)) {
            return sharedPreferences.getString(str, def)
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return if (sharedPreferences != null) {
            sharedPreferences.getString(str, def)
        } else def
    }

    fun getString(context: Context, str: String?): String? {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                val `object` = sSharedPreferenceMap[str]
                if (str == null) {
                    return null
                } else if (`object` is String) {
                    return `object`
                }
            }
        }
        var sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null && sharedPreferences.contains(str)) {
            return sharedPreferences.getString(str, null)
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences?.getString(str, null)
    }

    fun contains(context: Context, str: String?): Boolean {
        synchronized(sLock) {
            if (sSharedPreferenceMap.containsKey(str)) {
                return true
            }
        }
        var retVal = false
        var sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            retVal = sharedPreferences.contains(str)
        }
        if (retVal) {
            return true
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences?.contains(str) ?: false
    }

    fun remove(context: Context, str: String?) {
        synchronized(sLock) { sSharedPreferenceMap.remove(str) }
        var sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences != null && sharedPreferences.contains(str)) {
            val editor = sharedPreferences.edit()
            editor.remove(str)
            editor.apply()
            return
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (sharedPreferences != null) {
            val editor = sharedPreferences.edit()
            editor.remove(str)
            editor.apply()
        }
    }
}