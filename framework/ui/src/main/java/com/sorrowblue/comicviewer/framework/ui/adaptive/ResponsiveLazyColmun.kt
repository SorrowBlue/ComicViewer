package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.layout.copy
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar

@Composable
fun ResponsiveLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
    navigationState: NavigationState = LocalNavigationState.current,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        contentPadding = if (navigationState is NavigationState.NavigationBar) {
            contentPadding
        } else {
            if (navigationState.visible) {
                contentPadding.copy(start = 0.dp).copyWhenZero(skipStart = true)
            } else {
                contentPadding.copyWhenZero()
            }
        },
        state = state,
        verticalArrangement = if (navigationState is NavigationState.NavigationBar) {
            Arrangement.Top
        } else {
            Arrangement.spacedBy(ComicTheme.dimension.padding, Alignment.Top)
        },
        modifier = modifier.drawVerticalScrollbar(state)
    ) {
        content()
    }
}
