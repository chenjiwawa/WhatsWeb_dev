package com.qltech.messagesaver.common.utils

import com.qltech.common.utils.XLog.e
import java.io.Closeable
import java.io.IOException

object IOUtil {
    private val TAG = IOUtil::class.java.simpleName
    fun closeIO(vararg closeables: Closeable?) {
        for (closeable in closeables) {
            closeQuietly(closeable)
        }
    }

    @JvmStatic
    fun closeQuietly(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (e: IOException) {
                e(TAG, e.message!!, e)
            }
        }
    }
}