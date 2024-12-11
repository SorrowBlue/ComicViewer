package com.sorrowblue.comicviewer.feature.history.section

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.history.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalTopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton

internal sealed interface HistoryTopAppBarAction {
    data object Back : HistoryTopAppBarAction
    data object Settings : HistoryTopAppBarAction
    data object DeleteAll : HistoryTopAppBarAction
}

@Composable
internal fun HistoryTopAppBar(
    onAction: (HistoryTopAppBarAction) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CanonicalTopAppBar(
        title = { Text(stringResource(R.string.history_title)) },
        navigationIcon = { BackIconButton(onClick = { onAction(HistoryTopAppBarAction.Back) }) },
        actions = {
            IconButton(onClick = { onAction(HistoryTopAppBarAction.DeleteAll) }) {
                Icon(ComicIcons.Delete, null)
            }
            SettingsIconButton(onClick = { onAction(HistoryTopAppBarAction.Settings) })
        },
        scrollBehavior = scrollBehavior
    )
}
