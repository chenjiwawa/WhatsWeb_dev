package com.qltech.common.helper

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.qltech.common.extensions.isPermissionGranted
import com.qltech.common.utils.XLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class PermissionHelper(private val scope: CoroutineScope) {

    companion object {
        private const val TAG = "PermissionHelper"
    }

    private val permissionsQueue: MutableMap<Int, Channel<Map<String, Int>>> = HashMap()

    fun runOnPermissionGranted(
        activity: Activity,
        vararg permissions: String,
        onResult: (isGranted: Boolean) -> Unit
    ) {
        tryToGrantedPermission(activity, onResult, *permissions) { key: Int ->
            ActivityCompat.requestPermissions(activity, permissions, key)
        }
    }

    fun runOnPermissionGranted(
        fragment: Fragment,
        vararg permissions: String,
        onResult: (isGranted: Boolean) -> Unit
    ) {
        tryToGrantedPermission(fragment.context, onResult, *permissions) { key: Int ->
            fragment.requestPermissions(
                permissions,
                key
            )
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        scope.launch {
            val permissionResultsMap = permissions.mapIndexed { index, permission ->
                permission to grantResults[index]
            }.associate {
                it
            }
            permissionsQueue[requestCode]?.send(permissionResultsMap)
        }
    }

    private fun Array<out String>.permissionsToKey(): Int {
        return sorted().joinToString().hashCode() and 0xFFFF
    }

    private fun tryToGrantedPermission(context: Context?, onResult: (isGranted: Boolean) -> Unit, vararg permissions: String, requestPermissions: (key: Int) -> Unit){
        val isAlreadyGranted = permissions.all { permission ->
            true == context?.isPermissionGranted(permission)
        }
        if (isAlreadyGranted) {
            onResult(true)
            return
        }

        val key = permissions.permissionsToKey()
        val channel = Channel<Map<String, Int>>()
        permissionsQueue[key] = channel

        XLog.v(TAG, "[tryToGrantedPermission] requestPermissions: ${permissions.joinToString()}")
        requestPermissions(key)

        scope.launch {
            val grantResults = channel.receive()

            if (grantResults.isNotEmpty()) {
                val isGranted = grantResults.all {
                    it.value == PackageManager.PERMISSION_GRANTED
                }
                XLog.v(TAG, "[tryToGrantedPermission] onPermissionsResult($isGranted): $grantResults")
                onResult(isGranted)
            }

            permissionsQueue.remove(key)?.close()
        }
    }
}