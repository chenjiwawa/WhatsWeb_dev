package com.rockey.status.utils.update

import android.content.IntentSender.SendIntentException
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.qltech.common.utils.XLog
import com.qltech.ui.helper.SnackBarAdditional
import com.qltech.ui.model.SnackMessage
import com.qltech.whatsweb.R

/**
 * A simple implementation of the Android In-App Update API.
 *
 *
 * <div class="special reference">
 * <h3>In-App Updates</h3>
</div> *
 * For more information about In-App Updates you can check the official
 * [documentation](https://developer.android.com/guide/app-bundle/in-app-updates)
 *
 *
 */
open class InAppUpdateManager : LifecycleObserver, SnackBarAdditional {
    /**
     * Callback methods where update events are reported.
     */
    interface UpdateHandler {
        /**
         * On update error.
         *
         * @param code  the code
         * @param error the error
         */
        fun onInAppUpdateError(code: Int, error: Throwable)

        /**
         * Monitoring the update state of the flexible downloads.
         * For immediate updates, Google Play takes care of downloading and installing the update for you.
         *
         * @param status the status
         */
        fun onInAppUpdateStatus(manager: InAppUpdateManager, status: InAppUpdateStatus)

        fun onInAppUpdateNow(manager: InAppUpdateManager)
    }

    private var activity: AppCompatActivity
    private var requestCode = 64534
    private lateinit var appUpdateManager: AppUpdateManager
    private var snackBarMessage = "An update has just been downloaded."
    private var snackBarAction = "RESTART"
    private var mode = AppUpdateType.FLEXIBLE
    private var resumeUpdates = true
    private var useCustomNotification = false
    private var handler: UpdateHandler? =
        null
    private var snackbar: Snackbar? = null
    private val inAppUpdateStatus = InAppUpdateStatus()
    private val installStateUpdatedListener: InstallStateUpdatedListener =
        InstallStateUpdatedListener { installState ->
            inAppUpdateStatus.setInstallState(installState)
            reportStatus()

            // Show module progress, log state, or install the update.
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
                popupSnackBarForUserConfirmation()
            }
        }

    /**
     * @param activity    the activity
     */
    constructor(activity: AppCompatActivity) {
        this.activity = activity
        init()
    }

    /**
     * @param activity    the activity
     * @param requestCode the request code to later monitor this update request via onActivityResult()
     */
    constructor(activity: AppCompatActivity, requestCode: Int) {
        this.activity = activity
        this.requestCode = requestCode
        init()
    }

    private fun init() {
        appUpdateManager = AppUpdateManagerFactory.create(activity)
        activity.lifecycle.addObserver(this)
        if (mode == AppUpdateType.FLEXIBLE) appUpdateManager.registerListener(
            installStateUpdatedListener
        )
        checkForUpdate(false)
    }
    //endregion
    // region Setters
    /**
     * Set the update mode.
     *
     * @param mode the update mode
     * @return the update manager instance
     */
    fun mode(@AppUpdateType mode: Int): InAppUpdateManager {
        this.mode = mode
        return this
    }

    /**
     * Checks that the update is not stalled during 'onResume()'.
     * If the update is downloaded but not installed, will notify
     * the user to complete the update.
     *
     * @param resumeUpdates the resume updates
     * @return the update manager instance
     */
    fun resumeUpdates(resumeUpdates: Boolean): InAppUpdateManager {
        this.resumeUpdates = resumeUpdates
        return this
    }

    /**
     * Set the callback handler
     *
     * @param handler the handler
     * @return the update manager instance
     */
    fun handler(handler: UpdateHandler?): InAppUpdateManager {
        this.handler = handler
        return this
    }

    /**
     * Use custom notification for the user confirmation needed by the [AppUpdateType.FLEXIBLE] flow.
     * If this will set to true, need to implement the [UpdateHandler] and listen for the [InAppUpdateStatus.isDownloaded] status
     * via [UpdateHandler.onInAppUpdateStatus] callback. Then a notification (or some other UI indication) can be used,
     * to inform the user that installation is ready and requests user confirmation to restart the app. The confirmation must
     * call the [.completeUpdate] method to finish the update.
     *
     * @param useCustomNotification use custom user confirmation
     * @return the update manager instance
     */
    fun useCustomNotification(useCustomNotification: Boolean): InAppUpdateManager {
        this.useCustomNotification = useCustomNotification
        return this
    }

    fun snackBarMessage(snackBarMessage: String): InAppUpdateManager {
        this.snackBarMessage = snackBarMessage
        return this
    }

    fun snackBarAction(snackBarAction: String): InAppUpdateManager {
        this.snackBarAction = snackBarAction
        return this
    }

    fun snackBarActionColor(color: Int): InAppUpdateManager {
        snackbar!!.setActionTextColor(color)
        return this
    }

    //endregion
    //region Lifecycle
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (resumeUpdates) checkNewAppVersionState()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        unregisterListener()
    }
    //endregion
    //region Methods
    /**
     * Check for update availability. If there will be an update available
     * will start the update process with the selected [AppUpdateType].
     */
    fun checkForAppUpdate() {
        checkForUpdate(true)
    }

    /**
     * Triggers the completion of the app update for the flexible flow.
     */
    fun completeUpdate() {
        appUpdateManager!!.completeUpdate()
    }
    //endregion
    //region Private Methods
    /**
     * Check for update availability. If there will be an update available
     * will start the update process with the selected [AppUpdateType].
     */
    private fun checkForUpdate(startUpdate: Boolean) {

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask =
            appUpdateManager!!.appUpdateInfo


        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            inAppUpdateStatus.setAppUpdateInfo(appUpdateInfo)
            if (startUpdate) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    // Request the update.
                    if (mode == AppUpdateType.FLEXIBLE && appUpdateInfo.isUpdateTypeAllowed(
                            AppUpdateType.FLEXIBLE
                        )
                    ) {
                        // Start an update.
                        startAppUpdateFlexible(appUpdateInfo)
                    } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        // Start an update.
                        startAppUpdateImmediate(appUpdateInfo)
                    }
                    XLog.d(TAG,"checkForAppUpdate(): Update available. Version Code: " + appUpdateInfo.availableVersionCode())
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
                    XLog.d(TAG,"checkForAppUpdate(): No Update available. Code: " + appUpdateInfo.updateAvailability())
                }
            }
            reportStatus()
        }
    }

    private fun startAppUpdateImmediate(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,  // The current activity making the update request.
                activity,  // Include a request code to later monitor this update request.
                requestCode
            )
        } catch (e: SendIntentException) {
            XLog.d(TAG, "error in startAppUpdateImmediate", e)
            reportUpdateError(AppUpdateType.IMMEDIATE, e)
        }
    }

    private fun startAppUpdateFlexible(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,  // The current activity making the update request.
                activity,  // Include a request code to later monitor this update request.
                requestCode
            )
        } catch (e: SendIntentException) {
            XLog.d(TAG, "error in startAppUpdateFlexible", e)
            reportUpdateError(AppUpdateType.FLEXIBLE, e)
        }
    }

    /**
     * Displays the snackbar notification and call to action.
     * Needed only for Flexible app update
     */
    private fun popupSnackBarForUserConfirmation() {
        if (!useCustomNotification) {
            showSnackBar()
        }
    }

    /**
     * Checks that the update is not stalled during 'onResume()'.
     * However, you should execute this check at all app entry points.
     */
    private fun checkNewAppVersionState() {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                inAppUpdateStatus.setAppUpdateInfo(appUpdateInfo)

                //FLEXIBLE:
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackBarForUserConfirmation()
                    reportStatus()
                    XLog.d(TAG, "checkNewAppVersionState(): resuming flexible update. Code: " + appUpdateInfo.updateAvailability())
                }

                //IMMEDIATE:
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                        // 此处修改为 Flexible 更新，避免跳转到下载界面
                        startAppUpdateFlexible(appUpdateInfo)
                    XLog.d(TAG, "checkNewAppVersionState(): resuming flexible update. Code: " + appUpdateInfo.updateAvailability())
                }
            }
    }

    private fun showSnackBar() {
        activity.showSnackBar(
            SnackMessage(SnackMessage.Type.NORMAL, snackBarMessage),
            snackBarAction
        ) {  // Triggers the completion of the update of the app for the flexible flow.
            handler?.onInAppUpdateNow(this)
        }
    }

    private fun unregisterListener() {
        if (appUpdateManager != null && installStateUpdatedListener != null) appUpdateManager!!.unregisterListener(
            installStateUpdatedListener
        )
    }

    private fun reportUpdateError(errorCode: Int, error: Throwable) {
        if (handler != null) {
            handler!!.onInAppUpdateError(errorCode, error)
        }
    }

    private fun reportStatus() {
        if (handler != null) {
            handler!!.onInAppUpdateStatus(this, inAppUpdateStatus)
        }
    } //endregion

    companion object {
        // region Declarations
        private const val TAG = "InAppUpdateManager"

        /**
         * start to check for update
         *
         * @param activity    the activity
         * @param requestCode the request code to later monitor this update request via onActivityResult()
         */
        fun checkForUpdate(appCompatActivity: AppCompatActivity, requestCode: Int) {
            var inAppUpdateManager: InAppUpdateManager =
                InAppUpdateManager(appCompatActivity, requestCode)
                    .resumeUpdates(true) // Resume the update, if the update was stalled. Default is true
                    .mode(AppUpdateType.FLEXIBLE)
                    .snackBarMessage(appCompatActivity.getString(R.string.update_downloaded))
                    .snackBarAction(appCompatActivity.getString(R.string.restart_to_update))
                    .handler(InAppUpdateHandler())
            inAppUpdateManager.checkForAppUpdate()
        }
    }
}