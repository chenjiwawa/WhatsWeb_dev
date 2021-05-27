package com.qltech.base.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

interface ISharedPreferences {
    fun getSharedPreferences(): SharedPreferences
    fun getSharedPreferences(name: String): SharedPreferences
}

internal class SharedPreferencesHelper(private val context: Context) : ISharedPreferences {

    override fun getSharedPreferences(): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    override fun getSharedPreferences(name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

}