package com.qltech.base.helper

import android.content.Context
import java.io.File

interface IFile {
    fun getCacheFile(fileName: String): File
}

internal class FileHelper(private val context: Context) : IFile {

    override fun getCacheFile(fileName: String): File {
        val folder = context.cacheDir
        return createFile(folder, fileName)
    }

    private fun createFile(folder: File, fileName: String): File {
        if (!folder.isDirectory) {
            folder.mkdirs()
        }
        return File(folder, fileName)
    }
}