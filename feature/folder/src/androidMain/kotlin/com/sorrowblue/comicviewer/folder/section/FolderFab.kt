package com.sorrowblue.comicviewer.folder.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.animation.extendFabAnimation
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

internal sealed interface FolderFabAction {

    data object Up : FolderFabAction
    data object Down : FolderFabAction
}

@Composable
internal fun FolderFab(
    scrollTop: Boolean,
    scrollDown: Boolean,
    onAction: (FolderFabAction) -> Unit,
) {
    Column {
        AnimatedContent(
            targetState = scrollTop,
            transitionSpec = { extendFabAnimation() },
            label = "FirstItem"
        ) {
            if (it) {
                FloatingActionButton(onClick = { onAction(FolderFabAction.Up) }) {
                    Icon(imageVector = ComicIcons.ArrowUpward, contentDescription = null)
                }
            }
        }
        Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
        AnimatedContent(
            targetState = scrollDown,
            transitionSpec = { extendFabAnimation() },
            label = "EndItem"
        ) {
            if (it) {
                FloatingActionButton(onClick = { onAction(FolderFabAction.Down) }) {
                    Icon(imageVector = ComicIcons.ArrowDownward, contentDescription = null)
                }
            }
        }
    }
}
