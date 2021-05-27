package com.qltech.base.helper

import android.content.Context

interface IClassLoader {
    fun getApplicationClassLoader(): ClassLoader
}

internal class ClassLoaderHelper(private val context: Context) : IClassLoader {

    override fun getApplicationClassLoader(): ClassLoader = context.classLoader

}