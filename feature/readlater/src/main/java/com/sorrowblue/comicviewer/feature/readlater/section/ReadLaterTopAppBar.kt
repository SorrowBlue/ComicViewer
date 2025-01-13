package com.sorrowblue.comicviewer.feature.readlater.section

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.readlater.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalTopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton

internal sealed interface ReadLaterTopAppBarAction {
    data object ClearAll : ReadLaterTopAppBarAction
    data object Settings : ReadLaterTopAppBarAction
}

@Composable
internal fun ReadLaterTopAppBar(
    onAction: (ReadLaterTopAppBarAction) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    scrollableState: ScrollableState,
) {
    CanonicalTopAppBar(
        title = {
            Text(text = stringResource(id = R.string.readlater_title))
        },
        actions = {
            IconButton(onClick = { onAction(ReadLaterTopAppBarAction.ClearAll) }) {
                Icon(
                    imageVector = ComicIcons.ClearAll,
                    contentDescription = stringResource(R.string.readlater_action_clear_read_later)
                )
            }
            SettingsIconButton(onClick = { onAction(ReadLaterTopAppBarAction.Settings) })
        },
        scrollBehavior = scrollBehavior,
        scrollableState = scrollableState,
    )
}
