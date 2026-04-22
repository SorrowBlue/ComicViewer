package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.feature.settings.section.SettingsListPane
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiplatform
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.generated.resources.Res
import comicviewer.feature.settings.generated.resources.settings_label_display
import comicviewer.feature.settings.generated.resources.settings_label_extension
import comicviewer.feature.settings.generated.resources.settings_label_folder
import comicviewer.feature.settings.generated.resources.settings_label_info
import comicviewer.feature.settings.generated.resources.settings_label_language
import comicviewer.feature.settings.generated.resources.settings_label_security
import comicviewer.feature.settings.generated.resources.settings_label_viewer
import comicviewer.feature.settings.generated.resources.settings_title
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal data class SettingsScreenUiState(
    val currentSettings: SettingsItem = SettingsItem.DISPLAY,
    val settingsList: ImmutableList<SettingsItem> = SettingsItem.entries.toImmutableList(),
)

@Composable
internal fun SettingsScreen(
    uiState: SettingsScreenUiState,
    onBackClick: () -> Unit,
    onSettingsClick: (SettingsItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val paneScaffoldDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
    val lazyListState = rememberLazyListState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.settings_title)) },
                navigationIcon = { CloseIconButton(onBackClick) },
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
                ),
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        if (paneScaffoldDirective.maxHorizontalPartitions == 1) {
            LazyColumn(
                state = lazyListState,
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxSize(),
            ) {
                items(uiState.settingsList) { settings ->
                    ListItem(
                        headlineContent = { Text(text = stringResource(settings.title)) },
                        leadingContent = {
                            Icon(
                                imageVector = settings.icon,
                                contentDescription = null,
                            )
                        },
                        modifier = Modifier
                            .clickable(onClick = { onSettingsClick(settings) })
                            .testTag(settings.testTag),
                    )
                }
            }
        } else {
            SettingsListPane(
                settingsList = uiState.settingsList,
                currentSettings = uiState.currentSettings,
                onSettingsClick = onSettingsClick,
                onBackClick = onBackClick,
                modifier = Modifier.fillMaxSize(),
                lazyListState = lazyListState,
            )
        }
    }
}

internal enum class SettingsItem(
    val title: StringResource,
    val icon: ImageVector,
    val testTag: String,
) {
    DISPLAY(Res.string.settings_label_display, ComicIcons.DisplaySettings, "DisplaySettings"),
    FOLDER(Res.string.settings_label_folder, ComicIcons.FolderOpen, "FolderSettings"),
    VIEWER(Res.string.settings_label_viewer, ComicIcons.Image, "ViewerSettings"),
    SECURITY(Res.string.settings_label_security, ComicIcons.Lock, "SecuritySettings"),
    LANGUAGE(Res.string.settings_label_language, ComicIcons.Language, "LanguageSettings"),
    EXTENSION(Res.string.settings_label_extension, ComicIcons.Dataset, "ExtensionSettings"),
    HELP(Res.string.settings_label_info, ComicIcons.Info, "InfoSettings"),
}

@PreviewMultiplatform
@Composable
private fun SettingsScreenPreview() {
    PreviewTheme {
        val paneScaffoldDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())

        val a = rememberListDetailPaneScaffoldNavigator()
        ListDetailPaneScaffold(
            directive = paneScaffoldDirective,
            scaffoldState = a.scaffoldState,
            listPane = {
                SettingsScreen(
                    uiState = remember { SettingsScreenUiState() },
                    onBackClick = {},
                    onSettingsClick = {},
                )
            },
            detailPane = {
                Box(modifier = Modifier.fillMaxSize())
            },
        )
    }
}
