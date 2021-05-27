package com.qltech.base.helper

import android.content.Context
import android.util.DisplayMetrics
import androidx.annotation.DimenRes

interface IDimension {
    fun getDensity(): Float
    fun getDimension(@DimenRes dimension: Int): Float
}

internal class DimensionHelper(context: Context) : IDimension {

    private val resources = context.resources
    private val displayMetrics: DisplayMetrics = resources.displayMetrics

    override fun getDensity(): Float = displayMetrics.density

    override fun getDimension(dimension: Int): Float = resources.getDimension(dimension)

}