package com.qltech.messagesaver.common.utils

import com.qltech.messagesaver.common.utils.IOUtil.closeQuietly
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

object MD5 {
    /**
     * 获取文件的MD5
     *
     * @param file 要获取MD5值的文件
     * @return 文件的32位小写MD5字符串, 出现异常则返回空字符串
     */
    fun getFileMD5(file: File?): String {
        if (file == null) {
            return ""
        }
        var fis: FileInputStream? = null
        try {
            val digest = MessageDigest.getInstance("MD5")
            fis = FileInputStream(file)
            var len = 0
            val buffer = ByteArray(1024)
            while (fis.read(buffer).also { len = it } != -1) {
                digest.update(buffer, 0, len)
            }
            val result = digest.digest()
            return md5ToHex(result)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeQuietly(fis)
        }
        return ""
    }

    private fun md5ToHex(md5: ByteArray): String {
        if (md5.isEmpty()) {
            return ""
        }
        val sb = StringBuilder()
        for (byte in md5) {
            val b = byte.toInt()
            if (b and 0xFF < 0x10) sb.append("0")
            sb.append(Integer.toHexString(b and 0xFF))
        }
        return sb.toString()
    }
}