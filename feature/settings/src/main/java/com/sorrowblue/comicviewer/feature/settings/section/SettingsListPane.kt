package com.sorrowblue.comicviewer.feature.settings.section

import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.feature.settings.R
import com.sorrowblue.comicviewer.feature.settings.Settings2
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.material3.BackButton
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import kotlinx.collections.immutable.toPersistentList
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class SettingsListPaneUiState(
    val currentSettings2: Settings2 = Settings2.entries.first(),
    val list: List<Settings2> = Settings2.entries,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun SettingsListPane(
    navigator: ThreePaneScaffoldNavigator<Settings2>,
    onBackClick: () -> Unit,
    onSettingsClick: (Settings2) -> Unit,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val list = remember { Settings2.entries.toPersistentList() }
    val windowSizeClass = windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass
    if (windowSizeClass == WindowWidthSizeClass.COMPACT || windowSizeClass == WindowWidthSizeClass.MEDIUM) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.settings_title))
                    },
                    navigationIcon = {
                        BackButton(onClick = onBackClick)
                    },
                    scrollBehavior = scrollBehavior,
                    windowInsets = WindowInsets.safeDrawing
                        .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing,
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->
            LazyColumn(
                state = lazyListState,
                contentPadding = innerPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .drawVerticalScrollbar(lazyListState)
            ) {
                items(list) { settings2 ->
                    ListItem(
                        headlineContent = { Text(text = stringResource(id = settings2.title)) },
                        leadingContent = {
                            Icon(imageVector = settings2.icon, contentDescription = null)
                        },
                        modifier = Modifier
                            .clickable { onSettingsClick(settings2) }
                    )
                }
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.settings_title))
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = ComicIcons.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    ),
                    windowInsets = WindowInsets.safeDrawing
                        .only(WindowInsetsSides.Top + WindowInsetsSides.Start)
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing
                .only(WindowInsetsSides.Vertical + WindowInsetsSides.Start),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            LazyColumn(
                state = lazyListState,
                contentPadding = it.add(
                    paddingValues = PaddingValues(
                        start = 24.dp,
                        top = 8.dp,
                        end = 24.dp,
                        bottom = 8.dp
                    )
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .drawVerticalScrollbar(lazyListState)
            ) {
                items(list) { settings2 ->
                    ListItem(
                        headlineContent = { Text(text = stringResource(id = settings2.title)) },
                        leadingContent = {
                            Icon(imageVector = settings2.icon, contentDescription = null)
                        },
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.large)
                            .clickable { onSettingsClick(settings2) },
                        colors = ListItemDefaults.colors(
                            containerColor = if (navigator.currentDestination?.content == settings2) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            }
                        )
                    )
                }
            }
        }
    }
}
