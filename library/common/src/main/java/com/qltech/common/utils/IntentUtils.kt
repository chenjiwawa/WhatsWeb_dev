package com.qltech.common.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.qltech.common.R
import com.qltech.common.helper.FastActionFilter
import com.qltech.common.helper.IntentInterruptHelper
import java.io.File

object IntentUtils {

    private const val TAG = "IntentUtils"
    private val actionFilter = FastActionFilter()

    /**
     * @return True if intent is interrupt, so you need wait for activity Result
     */
    fun startActivity(context: Context?, intent: Intent): Boolean {
        if (null == context) return false
        if (!actionFilter.isValidAction()) return false

        val interrupt = IntentInterruptHelper.getInterrupt(intent)
        return if (null != interrupt) {
            if (context is Activity) {
                IntentInterruptHelper.createNewIntent(context, interrupt, intent)
                true
            } else {
                throw IllegalStateException("Intent is need interrupt, but context is not a activity")
            }
        } else {
            context.startActivity(intent)
            false
        }
    }

    /**
     * @return True if intent is interrupt, so you need wait for activity Result
     */
    fun startActivityForResult(activity: Activity?, intent: Intent, requestCode: Int): Boolean {
        if (null == activity) return false
        if (!actionFilter.isValidAction()) return false

        val interrupt = IntentInterruptHelper.getInterrupt(intent)
        return if (null != interrupt) {
            IntentInterruptHelper.createNewIntent(activity, interrupt, intent, requestCode)
            true
        } else {
            activity.startActivityForResult(intent, requestCode)
            false
        }
    }

    /**
     * @return True if intent is interrupt, so you need wait for activity Result
     */
    fun onActivityResult(
        activity: Activity,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean {
        return IntentInterruptHelper.onActivityResult(activity, data, requestCode, resultCode)
    }

    fun onActivityFinish(activity: Activity, intent: Intent?) {
        if (IntentInterruptHelper.isInterruptIntent(intent)) {
            activity.overridePendingTransition(
                R.anim.activity_close_enter,
                R.anim.activity_interrupt_exit
            )
        }
    }

    fun openUrl(context: Context, url: String, requestCode: Int? = null) {
        if (!actionFilter.isValidAction()) return

        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            if (null != requestCode && context is Activity) {
                context.startActivityForResult(intent, requestCode)
            } else {
                context.startActivity(intent)
            }
        } catch (e: ActivityNotFoundException) {
            XLog.e(TAG, "[openUrl]", e)
            ToastUtils.showToast(context.getString(R.string.toast_activity_not_found, uri.scheme))
        }
    }

    fun toAppSettings(context: Context) {
        if (!actionFilter.isValidAction()) return

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }

    fun toImageChooser(activity: Activity, requestCode: Int) {
        if (!actionFilter.isValidAction()) return

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        activity.startActivityForResult(
            Intent.createChooser(intent, activity.getString(R.string.intent_name_image_chooser)),
            requestCode
        )
    }

    /**
     * @param authority: BuildConfig.APPLICATION_ID + ".provider"
     */
    fun shareImage(context: Context, imageFile: File, authority: String, packageName: String? = null) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.type = "image/*"
        if (Build.VERSION.SDK_INT >= 24) {
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, authority, imageFile))
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile))
        }

        packageName?.run(intent::setPackage)
        context.startActivity(Intent.createChooser(intent, "Share Image"))
    }
}