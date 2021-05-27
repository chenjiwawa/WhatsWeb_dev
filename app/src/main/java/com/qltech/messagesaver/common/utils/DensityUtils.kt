package com.qltech.messagesaver.common.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue

/**
 * 单位转换 工具类<br></br>
 */
object DensityUtils {

    private var sWidth = 0


    private var sHeight = 0


    fun dp2px(dpVal: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dpVal,
            context.resources.displayMetrics
        ).toInt()
    }
    /**
     * dp转px
     */
    fun dp2px(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dpVal,
            context.resources.displayMetrics
        ).toInt()
    }

    /**
     * sp转px
     */
    fun sp2px(context: Context, spVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, spVal,
            context.resources.displayMetrics
        ).toInt()
    }

    /**
     * px转dp
     */
    fun px2dp(context: Context, pxVal: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxVal / scale).toInt()
    }

    /**
     * px转sp
     */
    fun px2sp(context: Context, pxVal: Float): Float {
        return (pxVal / context.resources.displayMetrics.scaledDensity).toInt().toFloat()
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenW(ctx: Context): Int {
        if (sWidth <= 0) {
            val dm: DisplayMetrics = ctx.resources.displayMetrics
            sWidth = dm.widthPixels
        }
        return sWidth
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenH(ctx: Context): Int {
        if (sHeight <= 0) {
            val dm: DisplayMetrics = ctx.resources.displayMetrics
            sHeight = dm.heightPixels
        }
        return sHeight
    }

    fun getStringInsert(nums: String, news_char: Char, location: Int): String {
        return nums.substring(0, location) + news_char + nums.substring(location, nums.length)
    }
}