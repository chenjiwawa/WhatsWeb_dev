package com.qltech.common.helper

import android.annotation.SuppressLint
import android.util.Log
import com.qltech.common.utils.TimeUtils
import com.qltech.base.helper.AppHelper
import com.qltech.base.helper.IFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat

class LogFile constructor(
    fileProvider: IFile = AppHelper.fileHelper
) {

    companion object {
        private const val FOLDER_NAME = "log"
        private const val FILE_COUNT_MAX = 10
        /**
         * File size limited is under 10MB
         * But log is record by one line,
         * It may over 10MB some time.
         * So for avoid over 10MB, this value set under 9MB
         * */
        private const val FILE_SIZE_MAX = 9 * 1024 * 1024

    }

    @SuppressLint("SimpleDateFormat")
    private var simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS")

    private val logFolder: File = fileProvider.getCacheFile(FOLDER_NAME)
    private lateinit var file: File

    init {
        createNewFile()
    }

    fun saveLog(priority: Int, tag: String, message: String) {
        if (priority < Log.INFO) return

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val fileSize = file.length()

                if (fileSize > FILE_SIZE_MAX) {
                    println("file size over : $fileSize")
                    createNewFile()
                }
                file.appendText(
                    "${simpleDateFormat.format(TimeUtils.currentTimeMillis())} ${getPriorityName(
                        priority
                    )}/$tag: $message\n"
                )
            } catch (e: FileNotFoundException) {
                // to avoid the user clear the cache during app runtime.
                e.printStackTrace()
                createNewFile()
            } catch (e: UninitializedPropertyAccessException) {
                e.printStackTrace()
                createNewFile()
            }

        }
    }

    private fun createNewFile() {
        file = File(logFolder, "${TimeUtils.currentTimeMillis()}.log")

        if (!logFolder.isDirectory) {
            logFolder.mkdirs()
        }

        if (!file.exists()) {
            file.createNewFile()
        }

        removeOldLog()
    }

    private fun getPriorityName(priority: Int): String {
        return when (priority) {
            Log.VERBOSE -> "V"
            Log.DEBUG -> "D"
            Log.INFO -> "I"
            Log.WARN -> "W"
            Log.ERROR -> "E"
            else -> ""
        }
    }

    private fun removeOldLog() {
        val listFiles: Array<File>? = logFolder.listFiles()
        if (listFiles.isNullOrEmpty() || listFiles.size < FILE_COUNT_MAX) return

        listFiles.map {
            Pair(getFileName(it).toLong(), it)
        }.sortedByDescending {
            it.first
        }.subList(FILE_COUNT_MAX, listFiles.size).forEach {
            it.second.delete()
        }
    }

    private fun getFileName(file: File): String {
        val dotIndex = file.name.indexOf('.')
        var result = file.name.substring(0, dotIndex)
        if (result.isEmpty()) {
            result = "0"
        }
        return result
    }
}