package com.qltech.common.helper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.qltech.common.R
import com.qltech.common.constant.BundleConstant
import com.qltech.common.utils.IntentUtils
import com.qltech.common.utils.XLog

object IntentInterruptHelper {

    private const val TAG = "IntentInterruptHelper"

    private const val VALUE_ACTION_START = "startActivity"
    private const val VALUE_ACTION_START_FOR_RESULT = "startActivityForResult"

    private val interrupts: MutableList<IInterrupter> = ArrayList()

    fun addInterrupt(interrupter: IInterrupter) {
        interrupts.add(interrupter)
    }

    fun addInterrupt(interrupters: List<IInterrupter>) {
        interrupts.addAll(interrupters)
    }

    fun getInterrupt(intent: Intent): IInterrupter? {
        return interrupts.find {
            it.isNeedInterrupt(intent)
        }?.also {
            XLog.d(
                TAG,
                "[getInterrupt] ${intent.component?.shortClassName} need interrupt by ${it.javaClass.simpleName}."
            )
        }
    }

    fun isInterruptIntent(intent: Intent?): Boolean {
        return null != intent?.extras?.getParcelable(BundleConstant.KEY_SOURCE_INTENT)
    }

    fun createNewIntent(
        activity: Activity,
        interrupter: IInterrupter,
        sourceIntent: Intent,
        requestCode: Int? = null
    ) {
        val action = if (null == requestCode) VALUE_ACTION_START else VALUE_ACTION_START_FOR_RESULT
        val targetClass = interrupter.getNewsClass()
        val newIntent = Intent(activity, targetClass)
        newIntent.putExtras(Bundle().apply {
            putString(BundleConstant.KEY_ACTION, action)
            putParcelable(BundleConstant.KEY_SOURCE_INTENT, sourceIntent)
            if (null != requestCode) {
                putInt(BundleConstant.KEY_REQUEST_CODE, requestCode)
            }
        })
        activity.startActivityForResult(newIntent, BundleConstant.REQUEST_CODE_INTERRUPT)
        activity.overridePendingTransition(
            R.anim.activity_interrupt_enter,
            R.anim.activity_open_exit
        )
        XLog.d(TAG, "[createNewIntent] Intent to ${targetClass.simpleName}.")
    }

    /**
     * @return True if intent is interrupt, so you need wait for activity Result
     */
    fun onActivityResult(
        activity: Activity,
        intent: Intent?,
        requestCode: Int,
        resultCode: Int
    ): Boolean {
        if (!isHandleResult(requestCode, resultCode)) {
            XLog.d(
                TAG,
                "[onActivityResult] Don't need to handle result. (requestCode: $requestCode, resultCode:, $resultCode)"
            )
            return false
        }

        val extras = intent?.extras
        if (null == extras) {
            XLog.w(TAG, "[onActivityResult] extras is null")
            return false
        }

        val sourceIntent: Intent? = extras.getParcelable(BundleConstant.KEY_SOURCE_INTENT)

        return if (null != sourceIntent) {
            val action: String = extras.getString(BundleConstant.KEY_ACTION) ?: ""

            XLog.d(TAG, "[onActivityResult] Intent to sourceIntent. ($sourceIntent)")
            if (VALUE_ACTION_START_FOR_RESULT == action) {
                val sourceRequestCode: Int = extras.getInt(BundleConstant.KEY_REQUEST_CODE)
                IntentUtils.startActivityForResult(activity, sourceIntent, sourceRequestCode)
            } else {
                IntentUtils.startActivity(activity, sourceIntent)
            }
        } else {
            XLog.w(TAG, "[onActivityResult] sourceIntent is null")
            false
        }
    }

    private fun isHandleResult(requestCode: Int, resultCode: Int): Boolean {
        return BundleConstant.REQUEST_CODE_INTERRUPT == requestCode && Activity.RESULT_OK == resultCode
    }

    interface IInterrupter {
        fun isNeedInterrupt(intent: Intent): Boolean
        fun getNewsClass(): Class<*>
    }
}