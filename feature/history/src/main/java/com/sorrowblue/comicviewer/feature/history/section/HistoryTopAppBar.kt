package com.sorrowblue.comicviewer.feature.history.section

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.history.R
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton

internal sealed interface HistoryTopAppBarAction {
    data object Back : HistoryTopAppBarAction
    data object Settings : HistoryTopAppBarAction
}

@Composable
internal fun HistoryTopAppBar(
    onAction: (HistoryTopAppBarAction) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.history_title)) },
        navigationIcon = { BackIconButton(onClick = { onAction(HistoryTopAppBarAction.Back) }) },
        actions = {
            SettingsIconButton(onClick = { onAction(HistoryTopAppBarAction.Settings) })
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        scrollBehavior = scrollBehavior
    )
}
