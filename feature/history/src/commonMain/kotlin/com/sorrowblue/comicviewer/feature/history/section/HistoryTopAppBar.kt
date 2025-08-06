package com.sorrowblue.comicviewer.feature.history.section

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.canonical.CanonicalAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton
import comicviewer.feature.history.generated.resources.Res
import comicviewer.feature.history.generated.resources.history_title
import org.jetbrains.compose.resources.stringResource

internal sealed interface HistoryTopAppBarAction {
    data object Back : HistoryTopAppBarAction
    data object Settings : HistoryTopAppBarAction
    data object DeleteAll : HistoryTopAppBarAction
}

@Composable
internal fun CanonicalScaffoldState<*>.HistoryTopAppBar(
    onAction: (HistoryTopAppBarAction) -> Unit,
) {
    CanonicalAppBar(
        title = { Text(stringResource(Res.string.history_title)) },
        navigationIcon = { BackIconButton(onClick = { onAction(HistoryTopAppBarAction.Back) }) },
        actions = {
            IconButton(onClick = { onAction(HistoryTopAppBarAction.DeleteAll) }) {
                Icon(ComicIcons.Delete, null)
            }
            SettingsIconButton(onClick = { onAction(HistoryTopAppBarAction.Settings) })
        },
    )
}
