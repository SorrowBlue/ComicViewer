package com.sorrowblue.comicviewer.feature.settings.info.license

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

internal interface LicenseScreenState {
    val uiState: LicenseScreenUiState
    val libs: Libs?

    fun onLibraryClick(library: Library)
    fun closeDialog()
}

@Composable
context(context: LicenseScreenContext)
internal fun rememberLicenseScreenState(): LicenseScreenState {
    val libs by produceLibraries {
        context.licenseeHelper.loadLibraries().decodeToString()
    }
    val uriHandler = LocalUriHandler.current
    return remember(libs, uriHandler) {
        LicenseScreenStateImpl(libs, uriHandler)
    }

}

private class LicenseScreenStateImpl(
    override val libs: Libs?,
    val uriHandler: UriHandler,
) : LicenseScreenState {

    override var uiState by mutableStateOf(LicenseScreenUiState(libs))

    override fun onLibraryClick(library: Library) {
        val license = library.licenses.firstOrNull()
        if (!license?.htmlReadyLicenseContent.isNullOrBlank()) {
            uiState = uiState.copy(openDialog = library)
        } else if (!license?.url.isNullOrBlank()) {
            license.url?.also {
                try {
                    uriHandler.openUri(it)
                } catch (t: Throwable) {
                    logcat(
                        tag = "LibrariesContainerFixed",
                        priority = LogPriority.ERROR
                    ) { t.asLog() }
                }
            }
        }
    }

    override fun closeDialog() {
        uiState = uiState.copy(openDialog = null)
    }
}
