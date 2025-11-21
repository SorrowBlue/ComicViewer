package com.sorrowblue.comicviewer.feature.settings.plugin.pdf

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
import com.sorrowblue.scomicviewer.domain.usecase.RegisterPdfPluginUseCase
import comicviewer.feature.settings.plugin.generated.resources.Res
import comicviewer.feature.settings.plugin.generated.resources.settings_plugin_error_not_found
import comicviewer.feature.settings.plugin.generated.resources.settings_plugin_error_not_supported_version
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.PickerResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal interface PdfPluginScreenState {
    val uiState: PdfPluginScreenUiState

    fun onOpenFolderClick()
}

@Composable
context(context: PdfPluginScreenContext)
internal fun rememberPdfPluginScreenState(): PdfPluginScreenState {
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    return remember {
        PdfPluginScreenStateImpl(
            uriHandler = uriHandler,
            registerPdfPluginUseCase = context.registerPdfPluginUseCase,
            managePdfPluginSettingsUseCase = context.managePdfPluginSettingsUseCase,
            coroutineScope = coroutineScope,
        )
    }.apply {
        directoryPickerLauncher = rememberDirectoryPickerLauncher(
            title = "ComicViewer PDFプラグインのインストールディレクトリを選択",
            onResult = ::onDirectoryPickerResult,
        )
    }
}

private class PdfPluginScreenStateImpl(
    private val uriHandler: UriHandler,
    private val registerPdfPluginUseCase: RegisterPdfPluginUseCase,
    private val managePdfPluginSettingsUseCase: ManagePdfPluginSettingsUseCase,
    private val coroutineScope: CoroutineScope,
) : PdfPluginScreenState {
    lateinit var directoryPickerLauncher: PickerResultLauncher

    override var uiState by mutableStateOf(PdfPluginScreenUiState())

    init {
        managePdfPluginSettingsUseCase.settings.onEach {
            uiState = uiState.copy(folderPath = it.pluginRootPath)
        }.launchIn(scope = coroutineScope)
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
                    val stringResource = when (it) {
                        RegisterPdfPluginUseCase.Error.NotFound -> Res.string.settings_plugin_error_not_found
                        RegisterPdfPluginUseCase.Error.NotSupportVersion -> Res.string.settings_plugin_error_not_supported_version
                    }
                    uiState =
                        uiState.copy(checking = false, info = "", error = getString(stringResource))
                }
        }
    }
}
