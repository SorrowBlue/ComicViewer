package com.sorrowblue.comicviewer.feature.settings.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.settings.R
import com.sorrowblue.comicviewer.feature.settings.Settings2
import com.sorrowblue.comicviewer.feature.settings.navgraphs.SettingsDetailNavGraph
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar

@Composable
internal fun SettingsListPane(
    navigator: ThreePaneScaffoldNavigator<Settings2>,
    onBackClick: () -> Unit,
    onSettingsClick: (Settings2) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val settingsList = remember { Settings2.entries + Settings2.entries + Settings2.entries }
    if (navigator.scaffoldDirective.maxHorizontalPartitions == 1) {
        CompactListPane(
            settingsList = settingsList,
            onSettingsClick = onSettingsClick,
            onBackClick = onBackClick,
            modifier = modifier,
            lazyListState = lazyListState
        )
    } else {
        ListPane(
            settingsList = settingsList,
            currentSettings = navigator.currentDestination?.contentKey
                ?: Settings2.entries.firstOrNull {
                    it.direction == SettingsDetailNavGraph.defaultStartDirection
                },
            onSettingsClick = onSettingsClick,
            onBackClick = onBackClick,
            modifier = modifier,
            lazyListState = lazyListState
        )
    }
}

@Composable
private fun CompactListPane(
    settingsList: List<Settings2>,
    onSettingsClick: (Settings2) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.settings_title)) },
                navigationIcon = { CloseIconButton(onBackClick) },
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        LazyColumn(
            state = lazyListState,
            contentPadding = contentPadding,
            modifier = Modifier
                .fillMaxSize()
                .drawVerticalScrollbar(lazyListState)
        ) {
            items(settingsList) { settings ->
                ListItem(
                    headlineContent = { Text(text = stringResource(id = settings.title)) },
                    leadingContent = {
                        Icon(
                            imageVector = settings.icon,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable { onSettingsClick(settings) }
                )
            }
        }
    }
}

@Composable
private fun ListPane(
    settingsList: List<Settings2>,
    currentSettings: Settings2?,
    onSettingsClick: (Settings2) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    PermanentDrawerSheet(
        modifier = modifier,
        drawerContainerColor = ComicTheme.colorScheme.surfaceContainerHighest,
        windowInsets = WindowInsets(0)
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = WindowInsets.safeDrawing.only(WindowInsetsSides.Start + WindowInsetsSides.Vertical)
                .asPaddingValues()
        ) {
            item {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.settings_title)) },
                    navigationIcon = { CloseIconButton(onBackClick) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ComicTheme.colorScheme.surfaceContainerHighest
                    ),
                    windowInsets = WindowInsets(0)
                )
            }
            items(settingsList) { settings2 ->
                NavigationDrawerItem(
                    label = { Text(text = stringResource(id = settings2.title)) },
                    icon = { Icon(imageVector = settings2.icon, contentDescription = null) },
                    onClick = { onSettingsClick(settings2) },
                    selected = currentSettings == settings2,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
