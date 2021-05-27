package com.qltech.base.helper

import android.content.Context
import com.qltech.base.BaseApplication

object AppHelper {

    private fun getContext(): Context = BaseApplication.getInstance().applicationContext

    val stringHelper: IString by lazy { StringHelper(getContext()) }

    val dimensionHelper: IDimension by lazy { DimensionHelper(getContext()) }

    val drawableHelper: IDrawable by lazy { DrawableHelper(getContext()) }

    val colorHelper: IColor by lazy { ColorHelper(getContext()) }

    val toastHelper: IToast by lazy { ToastHelper(getContext()) }

    val sharedPreferencesHelper: ISharedPreferences by lazy {
        SharedPreferencesHelper(
            getContext()
        )
    }

    val fileHelper: IFile by lazy { FileHelper(getContext()) }

    val classLoaderHelper: IClassLoader by lazy { ClassLoaderHelper(getContext()) }
}