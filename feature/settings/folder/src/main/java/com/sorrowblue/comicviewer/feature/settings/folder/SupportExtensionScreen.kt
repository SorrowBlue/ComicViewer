package com.sorrowblue.comicviewer.feature.settings.folder

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.feature.settings.common.CheckboxSetting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsColumn
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.FilledTonalButton
import com.sorrowblue.comicviewer.framework.ui.material3.Scaffold
import com.sorrowblue.comicviewer.framework.ui.material3.Text
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
internal fun SupportExtensionRoute(
    onBackClick: () -> Unit,
    state: SupportExtensionScreenState = rememberScreenState(),
) {
    val uiState = state.uiState
    SupportExtensionScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onExtensionToggle = state::toggleExtension,
        onExtensionClick = {}
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

@Composable
private fun SupportExtensionScreen(
    uiState: SupportExtensionScreenUiState,
    onBackClick: () -> Unit,
    onExtensionToggle: (SupportExtension) -> Unit,
    onExtensionClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = R.string.settings_folder_title_extension,
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        SettingsColumn(contentPadding = contentPadding) {
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
                        headlineContent = { Text(id = R.string.settings_folder_label_require_document) },
                        leadingContent = {
                            Icon(
                                imageVector = ComicIcons.Info,
                                contentDescription = null
                            )
                        }
                    )
                    FilledTonalButton(
                        text = R.string.settings_folder_label_install,
                        onClick = onExtensionClick,
                        modifier = Modifier
                            .padding(end = ComicTheme.dimension.margin)
                            .align(Alignment.End)
                    )
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
}
