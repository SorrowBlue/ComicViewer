package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import com.sorrowblue.comicviewer.domain.model.onError
import com.sorrowblue.comicviewer.domain.model.onSuccess
import com.sorrowblue.comicviewer.domain.usecase.settings.ManagePdfPluginSettingsUseCase
import com.sorrowblue.comicviewer.feature.tutorial.APP_DOWNLOAD_LINK
import com.sorrowblue.scomicviewer.domain.usecase.RegisterPdfPluginUseCase
import comicviewer.feature.tutorial.generated.resources.Res
import comicviewer.feature.tutorial.generated.resources.tutorial_msg_not_found_pdf_plugin
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.PickerResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

@Composable
context(context: DocumentSheetContext)
internal fun rememberDocumentSheetState(
    uriHandler: UriHandler = LocalUriHandler.current,
): DocumentSheetState {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        DocumentSheetStateImpl(
            managePdfPluginSettingsUseCase = context.managePdfPluginSettingsUseCase,
            uriHandler = uriHandler,
            registerPdfPluginUseCase = context.registerPdfPluginUseCase,
            coroutineScope = coroutineScope,
        )
    }.apply {
        directoryPickerLauncher = rememberDirectoryPickerLauncher(
            title = "ComicViewer PDFプラグインのインストールディレクトリを選択",
            onResult = ::onDirectoryPickerResult,
        )
    }
}

internal interface DocumentSheetState {
    val uiState: DocumentSheetUiState

    fun onDocumentDownloadClick()

    fun onOpenFolderClick()
}

private class DocumentSheetStateImpl(
    managePdfPluginSettingsUseCase: ManagePdfPluginSettingsUseCase,
    private val uriHandler: UriHandler,
    private val registerPdfPluginUseCase: RegisterPdfPluginUseCase,
    private val coroutineScope: CoroutineScope,
) : DocumentSheetState {
    lateinit var directoryPickerLauncher: PickerResultLauncher

    override var uiState by mutableStateOf(DocumentSheetUiState())

    init {
        managePdfPluginSettingsUseCase.settings.onEach {
            uiState = uiState.copy(folderPath = it.pluginRootPath)
        }.launchIn(scope = coroutineScope)
    }

    override fun onDocumentDownloadClick() {
        uriHandler.openUri(APP_DOWNLOAD_LINK)
    }

    override fun onOpenFolderClick() {
        uiState = uiState.copy(checking = true)
        directoryPickerLauncher.launch()
    }

    fun onDirectoryPickerResult(directory: PlatformFile?) {
        uiState = uiState.copy(checking = true)
        val path = directory?.file?.path ?: return run {
            uiState = uiState.copy(checking = false)
        }
        coroutineScope.launch {
            registerPdfPluginUseCase(RegisterPdfPluginUseCase.Request(path)).first()
                .onSuccess {
                    uiState = uiState.copy(
                        checking = false,
                        info = "プラグインの読み込みに成功しました",
                        error = "",
                    )
                }
                .onError {
                    val errorMsg = when (it) {
                        RegisterPdfPluginUseCase.Error.NotFound -> getString(
                            Res.string.tutorial_msg_not_found_pdf_plugin,
                        )

                        RegisterPdfPluginUseCase.Error.NotSupportVersion -> "古いバージョンです"
                    }
                    uiState = uiState.copy(checking = false, info = "", error = errorMsg)
                }
        }
    }
}
