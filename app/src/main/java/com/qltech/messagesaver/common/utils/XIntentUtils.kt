package com.qltech.messagesaver.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.qltech.whatsweb.BuildConfig
import java.io.File

object XIntentUtils {
    fun toAppInstall(context: Context, apkFile: File) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
                val apkUri = FileProvider.getUriForFile(
                    context,
                    "${BuildConfig.APPLICATION_ID}.provider",
                    apkFile
                )
                data = apkUri
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            val apkUri = Uri.fromFile(apkFile)
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}