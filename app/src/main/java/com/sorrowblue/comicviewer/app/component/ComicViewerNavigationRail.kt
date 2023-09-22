package com.sorrowblue.comicviewer.app.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.sorrowblue.comicviewer.app.MainScreenTab
import kotlinx.collections.immutable.PersistentList

@Composable
fun ComicViewerNavigationRail(
    mainScreenTabs: PersistentList<MainScreenTab>,
    onTabSelected: (MainScreenTab) -> Unit,
    currentTab: MainScreenTab?,
    modifier: Modifier = Modifier,
) {
    NavigationRail(
        modifier = modifier,
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start)
    ) {
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
