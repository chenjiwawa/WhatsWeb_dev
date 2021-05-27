package com.qltech.base.helper

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

interface IColor {
    @ColorInt
    fun getColor(@ColorRes res: Int): Int
}

internal class ColorHelper(private val context: Context) : IColor {

    override fun getColor(@ColorRes res: Int): Int = ContextCompat.getColor(context, res)

}