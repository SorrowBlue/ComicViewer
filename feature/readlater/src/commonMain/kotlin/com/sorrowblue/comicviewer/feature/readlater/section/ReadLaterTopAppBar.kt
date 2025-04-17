package com.sorrowblue.comicviewer.feature.readlater.section

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CanonicalTopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton
import comicviewer.feature.readlater.generated.resources.Res
import comicviewer.feature.readlater.generated.resources.readlater_action_clear_read_later
import comicviewer.feature.readlater.generated.resources.readlater_title
import org.jetbrains.compose.resources.stringResource

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
            Text(text = stringResource(Res.string.readlater_title))
        },
        actions = {
            IconButton(onClick = { onAction(ReadLaterTopAppBarAction.ClearAll) }) {
                Icon(
                    imageVector = ComicIcons.ClearAll,
                    contentDescription = stringResource(Res.string.readlater_action_clear_read_later)
                )
            }
            SettingsIconButton(onClick = { onAction(ReadLaterTopAppBarAction.Settings) })
        },
        scrollBehavior = scrollBehavior,
        scrollableState = scrollableState,
    )
}
