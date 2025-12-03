package com.sorrowblue.comicviewer.feature.history.section

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton
import comicviewer.feature.history.generated.resources.Res
import comicviewer.feature.history.generated.resources.history_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HistoryTopAppBar(
    onDeleteAllClick: () -> Unit,
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    AdaptiveAppBar(
        title = { Text(stringResource(Res.string.history_title)) },
        actions = {
            IconButton(onClick = onDeleteAllClick) {
                Icon(ComicIcons.Delete, null)
            }
            SettingsIconButton(onClick = onSettingsClick)
        },
        scrollBehavior = scrollBehavior,
    )
}
