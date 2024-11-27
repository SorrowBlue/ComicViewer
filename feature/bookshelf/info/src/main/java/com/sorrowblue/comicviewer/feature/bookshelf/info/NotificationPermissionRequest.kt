package com.sorrowblue.comicviewer.feature.bookshelf.info

import android.Manifest
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
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> action()

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) -> showInContextUI()

            else -> launchNotification()
        }
    }

    fun launchNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
