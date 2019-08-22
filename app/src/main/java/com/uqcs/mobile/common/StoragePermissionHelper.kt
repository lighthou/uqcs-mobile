package com.uqcs.mobile.common

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/** Helper to ask storage permission.  */
class StoragePermissionHelper {
    companion object {
        private val STORAGE_PERMISSION_CODE = 0
        private val STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE

        /** Check to see we have the necessary permissions for this app.  */
        fun hasStoragePermission(activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(activity, STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED
        }

        /** Check to see we have the necessary permissions for this app, and ask for them if we don't.  */
        fun requestStoragePermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(STORAGE_PERMISSION), STORAGE_PERMISSION_CODE
            )
        }

        /** Check to see if we need to show the rationale for this permission.  */
        fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, STORAGE_PERMISSION)
        }

        /** Launch Application Setting to grant permission.  */
        fun launchPermissionSettings(activity: Activity) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivity(intent)
        }
    }
}