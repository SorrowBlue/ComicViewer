package com.sorrowblue.comicviewer.feature.readlater.section

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.NavigationSuiteScaffold2State
import com.sorrowblue.comicviewer.framework.ui.canonical.CanonicalAppBar
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
internal fun NavigationSuiteScaffold2State<*>.ReadLaterTopAppBar(
    onAction: (ReadLaterTopAppBarAction) -> Unit,
) {
    CanonicalAppBar(
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
    )
}
