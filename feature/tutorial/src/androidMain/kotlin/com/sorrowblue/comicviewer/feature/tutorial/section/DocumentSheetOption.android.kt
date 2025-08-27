package com.sorrowblue.comicviewer.feature.tutorial.section

import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import logcat.LogPriority
import logcat.logcat

private const val pkg = "com.sorrowblue.comicviewer.plugin.pdf"
private const val supportMajorVersion = 0

private const val TAG = "DocumentSheetOption"

@Composable
internal actual fun DocumentSheetOption() {
    val state = rememberDocumentSheetOptionState()
    when (state.uiState.pluginState) {
        PdfPluginState.Enable -> {
            OutlinedButton(
                onClick = state::onOpenLinkClick,
                enabled = false,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(ComicIcons.OpenInBrowser, null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("PlayStoreを開く")
            }
            Spacer(Modifier.size(8.dp))
            Text("インストール済み")
        }

        PdfPluginState.OldVersion -> {
            OutlinedButton(
                onClick = state::onOpenLinkClick,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(ComicIcons.OpenInBrowser, null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("PlayStoreを開く")
            }
            Spacer(Modifier.size(8.dp))
            Text("インストールされているPDFプラグインのバージョンが古いです\n最新Verにアップデートしてください")
        }

        PdfPluginState.NotInstalled -> {
            OutlinedButton(
                onClick = state::onOpenLinkClick,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(ComicIcons.OpenInBrowser, null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("PlayStoreを開く")
            }
            Spacer(Modifier.size(8.dp))
            Text("プラグインがインストールされていません")
        }
    }
}

private data class DocumentSheetOptionUiState(
    val pluginState: PdfPluginState = PdfPluginState.NotInstalled,
)

private enum class PdfPluginState {
    Enable,
    OldVersion,
    NotInstalled
}

@Composable
private fun rememberDocumentSheetOptionState(
    uriHandler: UriHandler = LocalUriHandler.current,
    context: Context = LocalContext.current,
): DocumentSheetOptionState {
    val state =
        remember { DocumentSheetOptionStateImpl(uriHandler = uriHandler, context = context) }
    LifecycleResumeEffect(Unit) {
        state.onResume()
        onPauseOrDispose { }
    }
    return state
}

private interface DocumentSheetOptionState {
    fun onOpenLinkClick()

    val uiState: DocumentSheetOptionUiState
}

private class DocumentSheetOptionStateImpl(
    private val uriHandler: UriHandler,
    private val context: Context,
) : DocumentSheetOptionState {

    override var uiState by mutableStateOf(DocumentSheetOptionUiState())

    init {
        extracted()
    }

    private fun extracted() {
        val versionName = try {
            context.packageManager.getPackageInfo(pkg, 0)?.versionName
        } catch (_: NameNotFoundException) {
            logcat(TAG, LogPriority.INFO) { "Plugin '$pkg' is not installed" }
            uiState = uiState.copy(pluginState = PdfPluginState.NotInstalled)
            null
        }
        versionName?.let {
            val majorVersion = versionName.split(".")[0].toInt()
            uiState = if (supportMajorVersion <= majorVersion) {
                uiState.copy(pluginState = PdfPluginState.Enable)
            } else {
                uiState.copy(pluginState = PdfPluginState.OldVersion)
            }
        }
    }

    override fun onOpenLinkClick() {
        uriHandler.openUri("https://play.google.com/store/apps/details?id=$pkg")
    }

    fun onResume() {
        extracted()
    }
}
