package com.rockey.status.utils.update

import com.qltech.common.utils.XLog

class InAppUpdateHandler: InAppUpdateManager.UpdateHandler {

    companion object {
        private val TAG = InAppUpdateHandler::class.java.simpleName
    }

    override fun onInAppUpdateStatus(manager: InAppUpdateManager, status: InAppUpdateStatus) {
        /*
         * Called when the update status change occurred.
         */
        XLog.d(TAG, "onInAppUpdateStatus isUpdateAvailable:${status.isUpdateAvailable}\n availableVersionCode:${status.availableVersionCode()}\n isDownloading:${status.isDownloading}\n isDownloaded:${status.isDownloaded}\n isFailed:${status.isFailed}")
    }

    override fun onInAppUpdateNow(manager: InAppUpdateManager) {
        manager.completeUpdate()
    }

    override fun onInAppUpdateError(code: Int, error: Throwable) {
        /*
         * Called when some error occurred. See Constants class for more details
         */
        XLog.d(TAG, "onInAppUpdateError code:$code error:$error")
    }
}