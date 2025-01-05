package com.sorrowblue.comicviewer.feature.bookshelf.info

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

interface NotificationPermissionRequest {
    val activity: Activity
    val permissionLauncher: ManagedActivityResultLauncher<String, Boolean>

    fun requestPermission(action: () -> Unit, showInContextUI: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(
                activity,
                POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> action()

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                POST_NOTIFICATIONS
            ) -> showInContextUI()

            else -> launchNotification()
        }
    }

    fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                activity,
                POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }

    fun launchNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(POST_NOTIFICATIONS)
        }
    }
}
