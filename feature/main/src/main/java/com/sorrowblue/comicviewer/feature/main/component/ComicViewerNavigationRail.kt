package com.sorrowblue.comicviewer.feature.main.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.sorrowblue.comicviewer.feature.main.MainScreenTab
import kotlinx.collections.immutable.PersistentList

@Composable
fun ComicViewerNavigationRail(
    mainScreenTabs: PersistentList<MainScreenTab>,
    onTabSelected: (MainScreenTab) -> Unit,
    currentTab: MainScreenTab,
    modifier: Modifier = Modifier,
) {
    NavigationRail(
        modifier = modifier
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
