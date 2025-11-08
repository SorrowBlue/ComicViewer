package com.sorrowblue.comicviewer.feature.settings.section

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.settings.SettingsItem
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import comicviewer.feature.settings.generated.resources.Res
import comicviewer.feature.settings.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CompactListPane(
    settingsList: List<SettingsItem>,
    onSettingsClick: (SettingsItem) -> Unit,
    onSettingsLongClick: (SettingsItem) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
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
        LazyColumn(
            state = lazyListState,
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(settingsList) { settings ->
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
    }
}

@Composable
internal fun ListPane(
    settingsList: List<SettingsItem>,
    currentSettings: SettingsItem?,
    onSettingsClick: (SettingsItem) -> Unit,
    onSettingsLongClick: (SettingsItem) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
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
            items(settingsList) { settings2 ->
                NavigationDrawerItem(
                    label = { Text(text = stringResource(settings2.title)) },
                    icon = { Icon(imageVector = settings2.icon, contentDescription = null) },
                    onClick = { onSettingsClick(settings2) },
                    onLongClick = { onSettingsLongClick(settings2) },
                    selected = currentSettings == settings2,
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
fun NavigationDrawerItem(
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    badge: (@Composable () -> Unit)? = null,
    shape: Shape = CircleShape,
    colors: NavigationDrawerItemColors = NavigationDrawerItemDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    Surface(
        modifier = modifier
            .semantics { role = Role.Tab }
            .heightIn(min = 56.0.dp)
            .fillMaxWidth()
            .clip(shape)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    radius = Dp.Unspecified,
                    color = Color.Unspecified,
                ),
                onClick = { },
            ).combinedClickable(onClick = onClick, onLongClick = onLongClick),
        shape = shape,
        color = colors.containerColor(selected).value,
    ) {
        Row(
            Modifier.padding(start = 16.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                val iconColor = colors.iconColor(selected).value
                CompositionLocalProvider(LocalContentColor provides iconColor, content = icon)
                Spacer(Modifier.width(12.dp))
            }
            Box(Modifier.weight(1f)) {
                val labelColor = colors.textColor(selected).value
                CompositionLocalProvider(LocalContentColor provides labelColor, content = label)
            }
            if (badge != null) {
                Spacer(Modifier.width(12.dp))
                val badgeColor = colors.badgeColor(selected).value
                CompositionLocalProvider(LocalContentColor provides badgeColor, content = badge)
            }
        }
    }
}
