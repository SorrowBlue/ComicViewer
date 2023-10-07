package com.sorrowblue.comicviewer.app.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.app.MainScreenTab
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import kotlinx.collections.immutable.PersistentList

@Composable
fun AppNavigationBar(
    mainScreenTabs: PersistentList<MainScreenTab>,
    currentTab: MainScreenTab?,
    onTabSelected: (MainScreenTab) -> Unit,
) {
    NavigationBar(
        containerColor = ComicTheme.colorScheme.surfaceContainer,
    ) {
        mainScreenTabs.forEach { tab ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = stringResource(id = tab.contentDescription)
                    )
                },
                label = { Text(text = stringResource(id = tab.label)) }
            )
        }
    }
}
