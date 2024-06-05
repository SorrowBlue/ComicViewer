package com.sorrowblue.comicviewer.app.component

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class ComicViewerScaffoldUiState(
    val isAuthenticating: Boolean = false,
    val currentTab: MainScreenTab? = null,
    val tabs: PersistentList<MainScreenTab> = MainScreenTab.entries.toPersistentList(),
) : Parcelable {

    companion object : Parceler<ComicViewerScaffoldUiState> {
        override fun ComicViewerScaffoldUiState.write(parcel: Parcel, flags: Int) {
            parcel.writeBoolean(isAuthenticating)
            parcel.writeString(currentTab?.name)
            parcel.writeStringList(tabs.map(MainScreenTab::name))
        }

        override fun create(parcel: Parcel) = ComicViewerScaffoldUiState(
            parcel.readBoolean(),
            parcel.readString()?.let(MainScreenTab::valueOf),
            mutableListOf<String>().also(parcel::readStringList)
                .map(MainScreenTab::valueOf).toPersistentList()
        )
    }
}

@Composable
internal fun ComicViewerScaffold(
    uiState: ComicViewerScaffoldUiState,
    onTabSelect: (MainScreenTab) -> Unit,
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
        layoutType = navSuiteType,
        content = {
            Box(
                modifier = if (navSuiteType == NavigationSuiteType.NavigationBar) {
                    Modifier.consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                } else {
                    Modifier
                }
            ) {
                content()
            }
        }
    )
}
