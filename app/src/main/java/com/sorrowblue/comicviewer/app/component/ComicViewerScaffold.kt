package com.sorrowblue.comicviewer.app.component

import android.os.Parcelable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CompliantNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.calculateFromAdaptiveInfo2
import kotlinx.parcelize.Parcelize

/**
 * Comic viewer scaffold ui state
 *
 * @property isAuthenticating
 * @property currentTab
 * @property tabs
 * @constructor Create empty Comic viewer scaffold ui state
 */
@Parcelize
internal data class ComicViewerScaffoldUiState(
    val isAuthenticating: Boolean = false,
    val currentTab: MainScreenTab? = null,
    val tabs: List<MainScreenTab> = MainScreenTab.entries,
) : Parcelable

/**
 * Comic viewer scaffold
 *
 * @param uiState
 * @param onTabSelect
 * @param content
 * @receiver
 * @receiver
 */
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
                            contentDescription = stringResource(id = it.label)
                        )
                    },
                    label = {
                        Text(text = stringResource(id = it.label))
                    }
                )
            }
        },
        content = content
    )
}
