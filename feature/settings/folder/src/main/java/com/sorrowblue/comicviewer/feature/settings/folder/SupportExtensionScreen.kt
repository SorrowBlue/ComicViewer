package com.sorrowblue.comicviewer.feature.settings.folder

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.feature.settings.common.CheckboxSetting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane2
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
internal fun SupportExtensionRoute(
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
    state: SupportExtensionScreenState = rememberScreenState(),
) {
    val uiState = state.uiState
    SupportExtensionScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onExtensionToggle = state::toggleExtension,
        onExtensionClick = {},
        contentPadding = contentPadding
    )
}

internal data class SupportExtensionScreenUiState(
    val supportExtension: Set<SupportExtension> = emptySet(),
    val isDocumentInstalled: Boolean = false,
)

@Stable
internal class SupportExtensionScreenState(
    scope: CoroutineScope,
    val splitInstallManager: SplitInstallManager,
    private val viewModel: SupportExtensionViewModel,
) {

    var uiState by mutableStateOf(
        SupportExtensionScreenUiState(
            isDocumentInstalled = splitInstallManager.installedModules.contains("document")
        )
    )
        private set

    init {
        viewModel.settingsFlow.onEach {
            uiState = uiState.copy(supportExtension = it)
        }.launchIn(scope)
    }

    fun toggleExtension(supportExtension: SupportExtension) {
        viewModel.toggleExtension(supportExtension)
    }
}

@Composable
private fun rememberScreenState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: SupportExtensionViewModel = hiltViewModel(),
): SupportExtensionScreenState = remember {
    val splitInstallManager = SplitInstallManagerFactory.create(context)
    SupportExtensionScreenState(
        scope = scope,
        splitInstallManager = splitInstallManager,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupportExtensionScreen(
    uiState: SupportExtensionScreenUiState,
    onBackClick: () -> Unit,
    onExtensionToggle: (SupportExtension) -> Unit,
    onExtensionClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    SettingsDetailPane2(
        title = {
            Text(text = stringResource(id = R.string.settings_folder_title_extension))
        },
        onBackClick = onBackClick,
        contentPadding = contentPadding
    ) {
        SettingsCategory(title = R.string.settings_folder_label_archives) {
            SupportExtension.Archive.entries.forEach { extension ->
                CheckboxSetting(
                    title = extension.extension,
                    checked = uiState.supportExtension.contains(extension),
                    onCheckedChange = { onExtensionToggle(extension) }
                )
            }
        }
        SettingsCategory(title = R.string.settings_folder_label_document) {
            if (!uiState.isDocumentInstalled) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = R.string.settings_folder_label_require_document)
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = ComicIcons.Info,
                            contentDescription = null
                        )
                    }
                )
                FilledTonalButton(
                    onClick = onExtensionClick,
                    modifier = Modifier
                        .padding(end = ComicTheme.dimension.margin)
                        .align(Alignment.End)
                ) {
                    Text(text = stringResource(id = R.string.settings_folder_label_install))
                }
            }
            SupportExtension.Document.entries.forEach { extension ->
                CheckboxSetting(
                    title = extension.extension,
                    checked = uiState.isDocumentInstalled && uiState.supportExtension.contains(
                        extension
                    ),
                    onCheckedChange = { onExtensionToggle(extension) },
                    enabled = uiState.isDocumentInstalled
                )
            }
        }
    }
}
