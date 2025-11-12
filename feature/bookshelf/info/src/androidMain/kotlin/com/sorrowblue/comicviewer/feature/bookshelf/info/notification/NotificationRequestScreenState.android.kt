package com.sorrowblue.comicviewer.feature.bookshelf.info.notification

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal actual fun rememberNotificationRequestScreenState(): NotificationRequestScreenState =
    remember {
        NotificationRequestScreenStateImpl()
    }.apply {
        permissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    }

private class NotificationRequestScreenStateImpl : NotificationRequestScreenState {
    lateinit var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>

    override fun onConfirmClick(onComplete: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(POST_NOTIFICATIONS)
            onComplete()
        }
    }
}
