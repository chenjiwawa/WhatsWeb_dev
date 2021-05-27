package com.qltech.base.helper

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes

interface IString {
    fun getString(@StringRes res: Int): String
    fun getString(@StringRes res: Int, vararg any: Any): String

    fun getStringArray(@ArrayRes res: Int): Array<String>

}

internal class StringHelper(private val context: Context) : IString {
    override fun getStringArray(@ArrayRes res: Int): Array<String> =
        context.resources.getStringArray(res)

    override fun getString(@StringRes res: Int): String = context.getString(res)
    override fun getString(@StringRes res: Int, vararg any: Any): String =
        context.getString(res, *any)
}