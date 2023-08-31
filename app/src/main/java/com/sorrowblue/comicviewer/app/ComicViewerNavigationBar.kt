package com.sorrowblue.comicviewer.app

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.collections.immutable.PersistentList
import logcat.logcat

@Composable
fun ComicViewerNavigationBar(
    mainScreenTabs: PersistentList<MainScreenTab>,
    onTabSelected: (MainScreenTab) -> Unit,
    currentTab: MainScreenTab,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        mainScreenTabs.forEach { tab ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(imageVector = tab.icon, contentDescription = stringResource(id = tab.contentDescription)) },
                label = { Text(text = stringResource(id = tab.label)) }
            )
        }
    }
}
