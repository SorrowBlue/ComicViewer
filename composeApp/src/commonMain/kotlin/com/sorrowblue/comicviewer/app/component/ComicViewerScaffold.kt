package com.sorrowblue.comicviewer.app.component

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CompliantNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.calculateFromAdaptiveInfo2
import org.jetbrains.compose.resources.stringResource

internal data class ComicViewerScaffoldUiState(
    val currentTab: MainScreenTab? = null,
    val tabs: List<MainScreenTab> = MainScreenTab.entries,
)

@Composable
internal fun ComicViewerScaffold(
    uiState: ComicViewerScaffoldUiState,
    onTabSelect: (MainScreenTab) -> Unit,
    content: @Composable () -> Unit,
) {
    val info = currentWindowAdaptiveInfo()
    val navigationState by remember(info, uiState.currentTab != null) {
        mutableStateOf(
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo2(
                info,
                uiState.currentTab != null
            )
        )
    }
    CompliantNavigationSuiteScaffold(
        navigationState = navigationState,
        navigationSuiteItems = {
            uiState.tabs.forEach {
                item(
                    selected = it == uiState.currentTab,
                    onClick = { onTabSelect(it) },
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = stringResource(it.label)
                        )
                    },
                    label = { Text(text = stringResource(it.label)) }
                )
            }
        },
        content = content
    )
}
