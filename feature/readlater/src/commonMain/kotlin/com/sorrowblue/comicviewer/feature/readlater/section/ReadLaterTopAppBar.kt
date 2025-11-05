package com.sorrowblue.comicviewer.feature.readlater.section

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton
import comicviewer.feature.readlater.generated.resources.Res
import comicviewer.feature.readlater.generated.resources.readlater_action_clear_read_later
import comicviewer.feature.readlater.generated.resources.readlater_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ReadLaterTopAppBar(
    onClearAllClick: () -> Unit,
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = {
            Text(text = stringResource(Res.string.readlater_title))
        },
        actions = {
            IconButton(onClick = onClearAllClick) {
                Icon(
                    imageVector = ComicIcons.ClearAll,
                    contentDescription = stringResource(Res.string.readlater_action_clear_read_later)
                )
            }
            SettingsIconButton(onClick = onSettingsClick)
        },
        scrollBehavior = scrollBehavior
    )
}
