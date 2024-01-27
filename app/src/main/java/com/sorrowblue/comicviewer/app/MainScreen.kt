package com.sorrowblue.comicviewer.app

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class MainScreenUiState(
    val currentTab: MainScreenTab? = null,
    val tabs: PersistentList<MainScreenTab> = MainScreenTab.entries.toPersistentList(),
) : Parcelable {

    companion object : Parceler<MainScreenUiState> {
        override fun MainScreenUiState.write(parcel: Parcel, flags: Int) {
            parcel.writeString(currentTab?.name)
            parcel.writeStringList(tabs.map(MainScreenTab::name))
        }

        override fun create(parcel: Parcel) = MainScreenUiState(
            parcel.readString()?.let(MainScreenTab::valueOf),
            mutableListOf<String>().also(parcel::readStringList)
                .map(MainScreenTab::valueOf).toPersistentList()
        )
    }
}

@OptIn(
    ExperimentalMaterial3AdaptiveNavigationSuiteApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
internal fun MainScreen(
    uiState: MainScreenUiState,
    navController: NavHostController,
    onTabSelected: (NavController, MainScreenTab) -> Unit,
    content: @Composable () -> Unit,
) {
    val navSuiteType: NavigationSuiteType = if (uiState.currentTab != null) {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    } else {
        NavigationSuiteType.None
    }
    NavigationSuiteScaffold(
        modifier = if (navSuiteType == NavigationSuiteType.NavigationBar || navSuiteType == NavigationSuiteType.None) {
            Modifier
        } else {
            Modifier
                .background(ComicTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
        },
        navigationSuiteItems = {
            uiState.tabs.forEach {
                item(
                    selected = it == uiState.currentTab,
                    onClick = { onTabSelected(navController, it) },
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
        layoutType = navSuiteType,
        content = content
    )
}
