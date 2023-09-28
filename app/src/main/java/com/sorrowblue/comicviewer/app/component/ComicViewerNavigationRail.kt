package com.sorrowblue.comicviewer.app.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.app.MainScreenFab
import com.sorrowblue.comicviewer.app.MainScreenTab
import kotlinx.collections.immutable.PersistentList

@Composable
fun ComicViewerNavigationRail(
    mainScreenTabs: PersistentList<MainScreenTab>,
    onTabSelected: (MainScreenTab) -> Unit,
    currentTab: MainScreenTab?,
    currentFab: MainScreenFab?,
    onFabClick: (MainScreenFab) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRail(
        header = {
            AnimatedContent(
                targetState = currentFab,
                transitionSpec = { scaleIn() togetherWith scaleOut() },
                contentAlignment = Alignment.Center,
                label = "fab"
            ) { mainScreenFab ->
                if (mainScreenFab != null) {
                    FloatingActionButton(
                        onClick = { onFabClick(currentFab!!) },
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        modifier = Modifier.padding(top = 44.dp)
                    ) {
                        currentFab?.let { fab ->
                            Icon(
                                imageVector = fab.icon,
                                contentDescription = stringResource(id = fab.contentDescription)
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier,
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start)
    ) {
        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            mainScreenTabs.forEach { tab ->
                NavigationRailItem(
                    selected = currentTab == tab,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(id = tab.contentDescription)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = tab.label),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                )
            }
        }
    }
}
