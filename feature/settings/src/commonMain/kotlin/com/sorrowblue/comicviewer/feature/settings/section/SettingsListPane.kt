package com.sorrowblue.comicviewer.feature.settings.section

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.settings.SettingsItem
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import comicviewer.feature.settings.generated.resources.Res
import comicviewer.feature.settings.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SettingsListPane(
    settingsList: List<SettingsItem>,
    currentSettings: SettingsItem?,
    onSettingsClick: (SettingsItem) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    PermanentDrawerSheet(
        modifier = modifier,
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
                    colors = TopAppBarDefaults.topAppBarColors(),
                    windowInsets = WindowInsets(0),
                )
            }
            items(settingsList) { settings ->
                NavigationDrawerItem(
                    label = { Text(text = stringResource(settings.title)) },
                    icon = { Icon(imageVector = settings.icon, contentDescription = null) },
                    onClick = { onSettingsClick(settings) },
                    selected = currentSettings == settings,
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                )
            }
        }
    }
}
