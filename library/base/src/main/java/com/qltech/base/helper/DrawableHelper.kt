package com.qltech.base.helper

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

interface IDrawable {
    fun getDrawable(@DrawableRes res: Int): Drawable
}

internal class DrawableHelper(private val context: Context) : IDrawable {

    override fun getDrawable(@DrawableRes res: Int): Drawable {
        return ContextCompat.getDrawable(context, res)
            ?: throw throw IllegalStateException("Drawable is not found. ($res)")
    }
}