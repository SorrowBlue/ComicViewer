package com.sorrowblue.comicviewer.app.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.app.MainScreenTab
import com.sorrowblue.comicviewer.framework.designsystem.animation.navigationBarAnimation
import kotlinx.collections.immutable.PersistentList

@Composable
fun ComicViewerNavigationBar(
    mainScreenTabs: PersistentList<MainScreenTab>,
    currentTab: MainScreenTab?,
    onTabSelected: (MainScreenTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = currentTab != null,
        transitionSpec = { navigationBarAnimation() },
        contentAlignment = Alignment.BottomCenter,
        label = "ComicViewerNavigationBar"
    ) { isVisible ->
        if (isVisible) {
            NavigationBar(modifier = modifier) {
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
        } else {
            Spacer(Modifier.fillMaxWidth())
        }
    }
}
