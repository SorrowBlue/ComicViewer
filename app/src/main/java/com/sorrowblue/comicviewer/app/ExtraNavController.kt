package com.sorrowblue.comicviewer.app

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

internal interface ExtraNavController {
    fun startActivity(intent: Intent)
    fun launchUrl(url: String)
}

@Composable
internal fun rememberExtraNavController(context: Context = LocalContext.current): ExtraNavController =
    remember { ExtraNavControllerImpl(context) }

private class ExtraNavControllerImpl(val context: Context) : ExtraNavController {

    override fun startActivity(intent: Intent) {
        context.startActivity(intent)
    }

    override fun launchUrl(url: String) {
        CustomTabsIntent.Builder().build().launchUrl(context, url.toUri())
    }
}
