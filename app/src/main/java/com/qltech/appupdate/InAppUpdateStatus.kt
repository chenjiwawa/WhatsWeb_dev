package com.rockey.status.utils.update

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdateStatus {
    private var appUpdateInfo: AppUpdateInfo? = null
    private var installState: InstallState? = null
    fun setAppUpdateInfo(appUpdateInfo: AppUpdateInfo?) {
        this.appUpdateInfo = appUpdateInfo
    }

    fun setInstallState(installState: InstallState?) {
        this.installState = installState
    }

    val isDownloading: Boolean
        get() = if (installState != null) installState!!.installStatus() == InstallStatus.DOWNLOADING else false
    val isDownloaded: Boolean
        get() = if (installState != null) installState!!.installStatus() == InstallStatus.DOWNLOADED else false
    val isFailed: Boolean
        get() = if (installState != null) installState!!.installStatus() == InstallStatus.FAILED else false
    val isUpdateAvailable: Boolean
        get() = if (appUpdateInfo != null) appUpdateInfo!!.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE else false

    fun availableVersionCode(): Int {
        return if (appUpdateInfo != null) appUpdateInfo!!.availableVersionCode() else NO_UPDATE
    }

    companion object {
        private const val NO_UPDATE = 0
    }
}