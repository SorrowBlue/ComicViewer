package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.material3.ExpandedDockedSearchBar
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationBar

@Composable
fun CenteredSearchBar(
    navigationSuiteType: NavigationSuiteType,
    scrollBehavior: SearchBarScrollBehavior?,
    state: SearchBarState,
    inputField: @Composable (() -> Unit),
    content: @Composable () -> Unit,
) {
    TopSearchBar(
        scrollBehavior = scrollBehavior,
        state = state,
        inputField = inputField,
    )
    if (navigationSuiteType.isNavigationBar) {
        ExpandedFullScreenSearchBar(state = state, inputField = inputField) { content() }
    } else {
        ExpandedDockedSearchBar(state = state, inputField = inputField) {
            content()
        }
    }
}
