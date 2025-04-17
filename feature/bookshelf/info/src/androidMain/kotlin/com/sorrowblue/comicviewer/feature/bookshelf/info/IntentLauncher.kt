package com.sorrowblue.comicviewer.feature.bookshelf.info

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult

interface IntentLauncher {
    val intentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>

    fun launchIntent(intent: Intent) {
        intentLauncher.launch(intent)
    }
}
