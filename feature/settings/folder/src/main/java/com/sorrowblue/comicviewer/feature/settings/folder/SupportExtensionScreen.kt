package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.feature.settings.common.CheckboxSetting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsExtraNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsExtraPane
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Destination
@Composable
internal fun SupportExtensionScreen(
    contentPadding: PaddingValues,
    navigator: SettingsExtraNavigator,
) {
    SupportExtensionScreen(
        contentPadding = contentPadding,
        onBackClick = navigator::navigateUp
    )
}

@Composable
private fun SupportExtensionScreen(
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    state: SupportExtensionScreenState = rememberSupportExtensionScreenState(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupportExtensionScreen(
    uiState: SupportExtensionScreenUiState,
    onBackClick: () -> Unit,
    onExtensionToggle: (SupportExtension) -> Unit,
    onExtensionClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    SettingsExtraPane(
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
