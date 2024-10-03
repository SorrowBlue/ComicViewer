package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.animation.extendFabAnimation
import com.sorrowblue.comicviewer.framework.designsystem.animation.topAppBarAnimation
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalComponentColors
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.copy
import com.sorrowblue.comicviewer.framework.ui.preview.EdgeToEdgeTemplate
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme2
import kotlinx.coroutines.launch

@Composable
fun <T : Any> CanonicalScaffold(
    navigator: ThreePaneScaffoldNavigator<T>,
    modifier: Modifier = Modifier,
    topBar: @Composable (() -> Unit)? = null,
    extraPane: @Composable ((T) -> Unit)? = null,
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    alwaysVisibleFloatingActionButton: Boolean = false,
    content: @Composable (PaddingValues) -> Unit,
) {
    var fabHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Scaffold(
        floatingActionButton = {
            AnimatedContent(
                targetState = alwaysVisibleFloatingActionButton || navigator.scaffoldValue.tertiary == PaneAdaptedValue.Hidden,
                transitionSpec = { extendFabAnimation() },
                contentAlignment = Alignment.BottomEnd,
                label = "fab"
            ) {
                if (it) {
                    Box(
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.End))
                            .onGloballyPositioned {
                                with(density) {
                                    fabHeight = it.size.height.toDp() + 32.dp
                                }
                            }
                    ) {
                        floatingActionButton()
                    }
                } else {
                    fabHeight = 0.dp
                }
            }
        },
        topBar = {
            AnimatedContent(
                targetState = topBar != null && navigator.scaffoldValue.primary == PaneAdaptedValue.Expanded,
                transitionSpec = { topAppBarAnimation() },
                contentAlignment = Alignment.TopCenter,
                label = "topBar",
            ) {
                if (it && topBar != null) {
                    topBar()
                } else {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                                )
                            )
                    )
                }
            }
        },
        snackbarHost = snackbarHost,
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = LocalComponentColors.current.containerColor,
        modifier = modifier
    ) { contentPadding ->
        @Suppress("UNCHECKED_CAST")
        AnimatedExtraPaneScaffold(
            navigator = navigator as ThreePaneScaffoldNavigator<Any>,
            extraPane = {
                if (extraPane != null) {
                    var contentKey by remember {
                        mutableStateOf(navigator.currentDestination?.contentKey)
                    }
                    LaunchedEffect(navigator.currentDestination?.contentKey) {
                        if (navigator.currentDestination?.contentKey != null) {
                            contentKey = navigator.currentDestination?.contentKey
                        }
                    }
                    if (contentKey != null) {
                        val (consumeWindowInsets, top) =
                            if (LocalNavigationState.current is NavigationState.NavigationBar) {
                                WindowInsets(0) to 0.dp
                            } else {
                                contentPadding.asWindowInsets()
                                    .only(WindowInsetsSides.Start + WindowInsetsSides.Top) to contentPadding.calculateTopPadding()
                            }
                        Box(
                            Modifier
                                .padding(top = top)
                                .consumeWindowInsets(consumeWindowInsets)
                        ) {
                            extraPane.invoke(contentKey!!)
                        }
                    }
                }
            }
        ) {
            val skipStart = when (val navigationState = LocalNavigationState.current) {
                is NavigationState.NavigationBar -> false
                is NavigationState.NavigationDrawer -> navigationState.visible
                is NavigationState.NavigationRail -> navigationState.visible
            }
            val contentPadding1 =
                if (navigator.scaffoldDirective.maxHorizontalPartitions > 1 && navigator.scaffoldValue.tertiary == PaneAdaptedValue.Expanded) {
                    contentPadding.asWindowInsets().only(WindowInsetsSides.End)
                    contentPadding.copy(end = 0.dp)
                } else {
                    contentPadding
                }
            val animatePaddingValues by animatePaddingValuesAsState(
                contentPadding1.copyWhenZero(
                    skipStart = skipStart,
                    skipTop = topBar != null,
                    skipEnd = navigator.scaffoldDirective.maxHorizontalPartitions > 1 && navigator.scaffoldValue.tertiary == PaneAdaptedValue.Expanded
                )
                    .add(PaddingValues(bottom = fabHeight))
            )
            content(animatePaddingValues)
        }
    }
}

@Composable
@PreviewMultiScreen
private fun CanonicalScaffoldPreview(
    @PreviewParameter(NavigationProvider::class) item: CanonicalScaffoldPreviewItem,
) {
    PreviewTheme2(showNavigation = item.showNavigation, template = item.edgeToEdgeTemplate) {
        val navigator = rememberSupportingPaneScaffoldNavigator(
            initialDestinationHistory = listOf(item.destinationItem)
        )
        val scope = rememberCoroutineScope()
        CanonicalScaffold(
            navigator = navigator,
            topBar = {
                CanonicalTopAppBar(
                    title = {
                        Text("TopAppBar")
                    },
                )
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
                                navigator.navigateTo(SupportingPaneScaffoldRole.Extra, "extra")
                            }
                        }
                    }
                )
            },
            extraPane = { contentKey ->
                CanonicalExtraPaneScaffold(
                    title = { Text(contentKey) },
                    onCloseClick = {},
                ) {
                    ScratchBox(
                        color = Color.Green,
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize()
                    )
                }
            }
        ) {
            ScratchBox(
                color = Color.Red,
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            )
        }
    }
}

private data class CanonicalScaffoldPreviewItem(
    val topAppBar: Boolean,
    val destinationItem: ThreePaneScaffoldDestinationItem<String>,
    val edgeToEdgeTemplate: EdgeToEdgeTemplate,
    val showNavigation: Boolean = true,
)

private class NavigationProvider : PreviewParameterProvider<CanonicalScaffoldPreviewItem> {

    override val values = sequenceOf(
        CanonicalScaffoldPreviewItem(
            true,
            ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
            EdgeToEdgeTemplate()
        ),
        CanonicalScaffoldPreviewItem(
            false,
            ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
            EdgeToEdgeTemplate(isStatusBarVisible = false),
            showNavigation = false
        ),
        CanonicalScaffoldPreviewItem(
            true,
            ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "extra"),
            EdgeToEdgeTemplate()
        ),
        CanonicalScaffoldPreviewItem(
            true,
            ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
            EdgeToEdgeTemplate(isInvertedOrientation = true)
        ),
        CanonicalScaffoldPreviewItem(
            false,
            ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
            EdgeToEdgeTemplate(isInvertedOrientation = true, isStatusBarVisible = false)
        ),
        CanonicalScaffoldPreviewItem(
            true,
            ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "extra"),
            EdgeToEdgeTemplate(isInvertedOrientation = true)
        ),
    )
}
