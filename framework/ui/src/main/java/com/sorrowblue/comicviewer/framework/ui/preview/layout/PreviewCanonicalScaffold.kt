package com.sorrowblue.comicviewer.framework.ui.preview.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffold

@Composable
fun <T : Any> PreviewCanonicalScaffold(
    modifier: Modifier = Modifier,
    config: PreviewConfig = PreviewConfig(),
    navigator: ThreePaneScaffoldNavigator<T> = rememberSupportingPaneScaffoldNavigator<T>(),
    topBar: @Composable (() -> Unit)? = null,
    extraPane: @Composable ((T) -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    PreviewCompliantNavigation(config = config) {
        CanonicalScaffold(
            navigator = navigator,
            topBar = topBar,
            floatingActionButton = floatingActionButton,
            extraPane = extraPane,
            modifier = modifier,
            content = content
        )
    }
}
