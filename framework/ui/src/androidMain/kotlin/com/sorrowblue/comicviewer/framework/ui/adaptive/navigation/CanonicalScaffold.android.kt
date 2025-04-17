package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.layout.animateMainContentPaddingValues
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCompliantNavigation
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewConfig
import com.sorrowblue.comicviewer.framework.ui.preview.layout.scratch
import kotlinx.coroutines.launch

@PreviewMultiScreen
@Composable
private fun CanonicalScaffoldPreview(
    @PreviewParameter(CanonicalScaffoldConfigProvider::class) item: CanonicalScaffoldConfig,
) {
    PreviewCompliantNavigation(
        config = PreviewConfig(
            navigation = item.showNavigation,
            systemBar = item.systemBar,
            isInvertedOrientation = true
        )
    ) {
        val navigator = rememberSupportingPaneScaffoldNavigator(
            initialDestinationHistory = listOf(item.destinationItem)
        )
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val scope = rememberCoroutineScope()
        CanonicalScaffold(
            navigator = navigator,
            topBar = if (item.topAppBar) {
                {
                    CanonicalTopAppBar(
                        title = { Text("TopAppBar") },
                        scrollBehavior = scrollBehavior,
                    )
                }
            } else {
                null
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("extra") },
                    icon = { Icon(ComicIcons.Image, null) },
                    onClick = {
                        if (navigator.canNavigateBack()) {
                            scope.launch {
                                navigator.navigateBack()
                            }
                        } else {
                            scope.launch {
                                navigator.navigateTo(
                                    SupportingPaneScaffoldRole.Extra,
                                    "extra"
                                )
                            }
                        }
                    }
                )
            },
            extraPane = { contentKey ->
                ExtraPaneScaffold(
                    title = { Text(contentKey) },
                    onCloseClick = {},
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .scratch(Color.Red)
                            .padding(it)
                            .scratch(Color.Blue)
                    ) {
                        Text("Extra pane body")
                    }
                }
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { contentPadding ->
            val fillWidth = false
            val additionalPadding by animateMainContentPaddingValues(ignore = fillWidth)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .scratch(Color.Red)
                    .padding(contentPadding)
                    .scratch(Color.Blue)
                    .padding(additionalPadding)
                    .scratch(Color.Green)
            ) {
                Text("Main pane body")
            }
        }
    }
}

private data class CanonicalScaffoldConfig(
    val topAppBar: Boolean,
    val showNavigation: Boolean,
    val systemBar: Boolean,
    val destinationItem: ThreePaneScaffoldDestinationItem<String>,
)

private class CanonicalScaffoldConfigProvider : PreviewParameterProvider<CanonicalScaffoldConfig> {
    override val values: Sequence<CanonicalScaffoldConfig> =
        listOf(true, false).flatMap { topAppBar ->
            listOf(true, false).flatMap { showNavigation ->
                listOf(true, false).flatMap { systemBar ->
                    listOf(
                        ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
                        ThreePaneScaffoldDestinationItem(
                            SupportingPaneScaffoldRole.Extra,
                            "Extra pane"
                        )
                    ).map { destinationItem ->
                        CanonicalScaffoldConfig(
                            topAppBar = topAppBar,
                            showNavigation = showNavigation,
                            systemBar = systemBar,
                            destinationItem = destinationItem
                        )
                    }
                }
            }
        }.asSequence()
}
