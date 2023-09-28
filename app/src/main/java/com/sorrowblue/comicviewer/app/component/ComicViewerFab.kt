package com.sorrowblue.comicviewer.app.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.app.MainScreenFab
import com.sorrowblue.comicviewer.framework.designsystem.animation.fabAnimation

@Composable
internal fun ComicViewerFab(
    currentFab: MainScreenFab?,
    canScroll: Boolean,
    onClick: () -> Unit,
) {
    AnimatedContent(
        targetState = currentFab,
        transitionSpec = { fabAnimation() },
        contentAlignment = Alignment.BottomEnd,
        label = "fab_animation"
    ) { mainScreenFab ->
        if (mainScreenFab != null) {
            ExtendedFloatingActionButton(
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                expanded = !canScroll,
                text = {
                    Text(text = stringResource(id = mainScreenFab.label))
                },
                icon = {
                    Icon(
                        imageVector = mainScreenFab.icon,
                        contentDescription = stringResource(id = mainScreenFab.contentDescription)
                    )
                },
                onClick = onClick,
            )
        } else {
            Spacer(Modifier.size(1.dp))
        }
    }
}
