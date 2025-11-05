package com.sorrowblue.comicviewer.feature.bookshelf.info

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import logcat.logcat

interface NotificationPermissionRequest {
    val activity: Activity
    val permissionLauncher: ManagedActivityResultLauncher<String, Boolean>

    fun requestPermission(action: () -> Unit, showInContextUI: () -> Unit) = when {
        ContextCompat.checkSelfPermission(activity, POST_NOTIFICATIONS) == PERMISSION_GRANTED -> {
            logcat { "android.permission.POST_NOTIFICATIONS is granted" }
            action()
        }

        ActivityCompat.shouldShowRequestPermissionRationale(activity, POST_NOTIFICATIONS) -> {
            logcat { "Need to show UI that shows rationale for permission" }
            action()
            showInContextUI()
        }

        else -> {
            logcat { "Request android.permission.POST_NOTIFICATIONS" }
            launchNotification()
        }
    }

    fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                activity,
                POST_NOTIFICATIONS
            ) == PERMISSION_GRANTED
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
