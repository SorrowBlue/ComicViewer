package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.usecase.settings.ManagePdfPluginSettingsUseCase
import com.sorrowblue.comicviewer.feature.tutorial.APP_DOWNLOAD_LINK
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenContext
import com.sorrowblue.comicviewer.feature.tutorial.immatureRectangleProgressBorder
import com.sorrowblue.comicviewer.framework.common.LocalPlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalCoroutineScope
import comicviewer.feature.tutorial.generated.resources.Res
import comicviewer.feature.tutorial.generated.resources.tutorial_msg_found_pdf_plugin
import comicviewer.feature.tutorial.generated.resources.tutorial_msg_not_found_pdf_plugin
import comicviewer.feature.tutorial.generated.resources.tutorial_text_document_btn_download
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.PickerResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import logcat.logcat
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

data class DocumentSheetUiState(
    val folderPath: String = "",
    val checking: Boolean = false,
    val error: String = "",
    val info: String = "",
)

@Composable
internal actual fun DocumentSheetOption(modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val state = rememberDocumentSheetState()
        val uiState = state.uiState
        TextButton(onClick = state::onDocumentDownloadClick) {
            Row {
                Icon(ComicIcons.InstallMobile, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(Res.string.tutorial_text_document_btn_download))
            }
        }
        OutlinedTextField(
            label = {
                Text("インストールフォルダを選択")
            },
            value = uiState.folderPath,
            onValueChange = {},
            readOnly = true,
            enabled = !uiState.checking,
            supportingText = {
                if (uiState.error.isNotEmpty()) {
                    Text(uiState.error, color = ComicTheme.colorScheme.error)
                } else if (uiState.info.isNotEmpty()) {
                    Text(uiState.info)
                }
            },
            trailingIcon = {
                if (uiState.checking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                    )
                } else {
                    IconButton(
                        onClick = state::onSettingsClick,
                    ) {
                        Icon(ComicIcons.FolderOpen, contentDescription = null)
                    }
                }
            },
            modifier = Modifier
                .testTag("FolderSelect")
                .immatureRectangleProgressBorder(
                    color = ComicTheme.colorScheme.secondary,
                    enable = uiState.checking,
                ),
        )
    }
}

@Composable
internal fun rememberDocumentSheetState(
    uriHandler: UriHandler = LocalUriHandler.current,
    scope: CoroutineScope = LocalCoroutineScope.current,
): DocumentSheetState {
    val managePdfPluginSettingsUseCase =
        LocalPlatformContext.current.require<TutorialScreenContext.Factory>()
            .createTutorialScreenContext().managePdfPluginSettingsUseCase
    val state =
        remember { DocumentSheetStateImpl(uriHandler, managePdfPluginSettingsUseCase, scope) }
    val pickerResultLauncher =
        rememberDirectoryPickerLauncher("ComicViewer PDFプラグインのインストールディレクトリを選択") {
            state.onDirectoryPickerResult(it)
        }
    state.directoryPickerLauncher = pickerResultLauncher
    return state
}

internal interface DocumentSheetState {
    val uiState: DocumentSheetUiState

    fun onDocumentDownloadClick()

    fun onSettingsClick()
}

private class DocumentSheetStateImpl(
    private val uriHandler: UriHandler,
    private val settingsUseCase: ManagePdfPluginSettingsUseCase,
    private val scope: CoroutineScope,
) : DocumentSheetState {
    lateinit var directoryPickerLauncher: PickerResultLauncher

    override var uiState by mutableStateOf(DocumentSheetUiState())

    override fun onDocumentDownloadClick() {
        uriHandler.openUri(APP_DOWNLOAD_LINK)
    }

    override fun onSettingsClick() {
        directoryPickerLauncher.launch()
    }

    fun onDirectoryPickerResult(directory: PlatformFile?) {
        uiState = uiState.copy(checking = true)
        scope.launch {
            logcat { "path=${directory?.path}" }
            directory
                ?.file
                ?.resolve("app")
                ?.listFiles {
                    logcat { "path=${it.path}" }
                    it.name.startsWith("pdf-desktop-") && it.extension == "jar"
                }?.firstOrNull()
                ?.let { jarFile ->
                    // OK
                    settingsUseCase.edit {
                        it.copy(
                            pluginJarPath = jarFile.absolutePath,
                            pluginRootPath = directory.path,
                        )
                    }
                    uiState = uiState.copy(folderPath = directory.path)
                    uiState = uiState.copy(
                        error = "",
                        info = getString(Res.string.tutorial_msg_found_pdf_plugin),
                    )
                    delay(500)
                    uiState = uiState.copy(checking = false)
                } ?: run {
                uiState = uiState.copy(checking = false)
                uiState = uiState.copy(
                    error = getString(Res.string.tutorial_msg_not_found_pdf_plugin),
                    info = "",
                )
            }
        }
    }
}
