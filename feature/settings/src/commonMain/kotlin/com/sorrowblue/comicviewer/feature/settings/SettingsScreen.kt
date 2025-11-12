package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.settings.section.NavigationDrawerItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.composeicons.Plugin
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import comicviewer.feature.settings.generated.resources.Res
import comicviewer.feature.settings.generated.resources.settings_label_app
import comicviewer.feature.settings.generated.resources.settings_label_display
import comicviewer.feature.settings.generated.resources.settings_label_folder
import comicviewer.feature.settings.generated.resources.settings_label_image_cache
import comicviewer.feature.settings.generated.resources.settings_label_language
import comicviewer.feature.settings.generated.resources.settings_label_plugin
import comicviewer.feature.settings.generated.resources.settings_label_security
import comicviewer.feature.settings.generated.resources.settings_label_tutorial
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
    onSettingsLongClick: (SettingsItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val paneScaffoldDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo())
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
                        modifier = Modifier.combinedClickable(
                            onClick = { onSettingsClick(settings) },
                            onLongClick = { onSettingsLongClick(settings) },
                        ),
                    )
                }
            }
        } else {
            PermanentDrawerSheet(
                modifier = modifier,
                drawerContainerColor = ComicTheme.colorScheme.surfaceContainerHighest,
                windowInsets = WindowInsets(0),
            ) {
                LazyColumn(
                    state = lazyListState,
                    contentPadding = WindowInsets.safeDrawing
                        .only(
                            WindowInsetsSides.Start + WindowInsetsSides.Vertical,
                        ).asPaddingValues(),
                ) {
                    item {
                        TopAppBar(
                            title = { Text(text = stringResource(Res.string.settings_title)) },
                            navigationIcon = { CloseIconButton(onBackClick) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = ComicTheme.colorScheme.surfaceContainerHighest,
                            ),
                            windowInsets = WindowInsets(0),
                        )
                    }
                    items(uiState.settingsList) { settings2 ->
                        NavigationDrawerItem(
                            label = { Text(text = stringResource(settings2.title)) },
                            icon = {
                                Icon(
                                    imageVector = settings2.icon,
                                    contentDescription = null,
                                )
                            },
                            onClick = { onSettingsClick(settings2) },
                            onLongClick = { onSettingsLongClick(settings2) },
                            selected = uiState.currentSettings == settings2,
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

enum class SettingsItem(val title: StringResource, val icon: ImageVector) {
    DISPLAY(Res.string.settings_label_display, ComicIcons.DisplaySettings),
    FOLDER(Res.string.settings_label_folder, ComicIcons.FolderOpen),
    VIEWER(Res.string.settings_label_viewer, ComicIcons.Image),
    SECURITY(Res.string.settings_label_security, ComicIcons.Lock),
    APP(Res.string.settings_label_app, ComicIcons.Info),
    TUTORIAL(Res.string.settings_label_tutorial, ComicIcons.Start),

    Thumbnail(Res.string.settings_label_image_cache, ComicIcons.Storage),
    Plugin(Res.string.settings_label_plugin, ComicIcons.Plugin),
    LANGUAGE(Res.string.settings_label_language, ComicIcons.Language),
}
