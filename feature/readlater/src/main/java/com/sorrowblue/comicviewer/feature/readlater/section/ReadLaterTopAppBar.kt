package com.sorrowblue.comicviewer.feature.readlater.section

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.readlater.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsButton

internal sealed interface ReadLaterTopAppBarAction {
    data object ClearAll : ReadLaterTopAppBarAction
    data object Settings : ReadLaterTopAppBarAction
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReadLaterTopAppBar(
    onAction: (ReadLaterTopAppBarAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    androidx.compose.material3.TopAppBar(
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
            SettingsButton(onClick = { onAction(ReadLaterTopAppBarAction.Settings) })
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}
