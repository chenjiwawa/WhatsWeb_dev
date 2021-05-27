package com.qltech.messagesaver.common.utils

import java.io.*

object FileUtils {
    const val DEPUTY_JPG = ".jpg"
    const val DEPUTY_GIF = ".gif"
    const val DEPUTY_MP4 = ".mp4"

    fun isMp4(file: File): Boolean {
        return file.name.endsWith(DEPUTY_MP4)
    }

    fun copyFile(srcFile: File, dstFile: File): Boolean {
        dstFile.parentFile?.mkdirs()

        if (srcFile.exists() && srcFile.isFile) {
            if (dstFile.exists()) {
                dstFile.delete()
            }
            try {
                val buffer = ByteArray(2048)
                val input = BufferedInputStream(
                    FileInputStream(srcFile))
                val output = BufferedOutputStream(
                    FileOutputStream(dstFile))
                while (true) {
                    val count = input.read(buffer)
                    if (count == -1) {
                        break
                    }
                    output.write(buffer, 0, count)
                }
                input.close()
                output.flush()
                output.close()
                return true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }
}