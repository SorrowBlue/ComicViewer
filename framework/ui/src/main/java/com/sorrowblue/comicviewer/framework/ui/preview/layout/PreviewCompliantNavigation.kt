package com.sorrowblue.comicviewer.framework.ui.preview.layout

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CompliantNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.calculateFromAdaptiveInfo2
import com.sorrowblue.comicviewer.framework.ui.preview.fake.nextLoremIpsum

@Composable
fun PreviewCompliantNavigation(
    modifier: Modifier = Modifier,
    config: PreviewConfig = PreviewConfig(),
    content: @Composable () -> Unit,
) {
    PreviewDevice(config = config) {
        val navigationState =
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo2(currentWindowAdaptiveInfo())
        CompliantNavigationSuiteScaffold(
            navigationSuiteItems = {
                repeat(5) {
                    item(
                        selected = it == 0,
                        onClick = {},
                        icon = { Icon(ComicIcons.Folder, null) },
                        label = { Text(nextLoremIpsum().take(8)) }

                    )
                }
            },
            navigationState = if (config.navigation) {
                navigationState
            } else {
                when (navigationState) {
                    is NavigationState.NavigationBar -> navigationState.copy(false)
                    is NavigationState.NavigationRail -> navigationState.copy(false)
                }
            },
            modifier = modifier
        ) {
            content()
        }
    }
}
