package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.animation.fabEnter
import com.sorrowblue.comicviewer.framework.designsystem.animation.fabExit
import com.sorrowblue.comicviewer.framework.designsystem.animation.topAppBarAnimation
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigableExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.plus
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCompliantNavigation
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewConfig
import com.sorrowblue.comicviewer.framework.ui.preview.layout.scratch
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val LocalCoroutineScope = compositionLocalOf { CoroutineScope(EmptyCoroutineContext) }

@Composable
fun <T : Any> CanonicalScaffold(
    navigator: ThreePaneScaffoldNavigator<T>,
    modifier: Modifier = Modifier,
    topBar: @Composable (() -> Unit)? = null,
    extraPane: @Composable ((T) -> Unit)? = null,
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    var fabHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    CompositionLocalProvider(LocalCoroutineScope provides rememberCoroutineScope()) {
        Scaffold(
            floatingActionButton = {
                AnimatedVisibility(
                    navigator.scaffoldState.targetState.tertiary == PaneAdaptedValue.Hidden,
                    enter = fabEnter(),
                    exit = fabExit(),
                    label = "fab"
                ) {
                    Box(
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.End))
                            .onGloballyPositioned {
                                with(density) {
                                    fabHeight = it.size.height.toDp() + FabSpacing
                                }
                            }
                    ) {
                        floatingActionButton()
                    }
                }
            },
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = snackbarHost,
            modifier = modifier
        ) { padding ->
            NavigableExtraPaneScaffold(
                navigator = navigator,
                extraPane = {
                    if (extraPane != null) {
                        navigator.currentDestination?.contentKey?.let { contentKey ->
                            extraPane(contentKey)
                        }
                    }
                },
            ) {
                Scaffold(
                    topBar = if (topBar != null) {
                        {
                            AnimatedContent(
                                targetState = navigator.scaffoldValue.primary == PaneAdaptedValue.Expanded,
                                transitionSpec = { topAppBarAnimation() },
                                contentAlignment = Alignment.TopCenter,
                                label = "topBar",
                            ) {
                                if (it) {
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
                        }
                    } else {
                        {}
                    },
                    contentWindowInsets = padding.asWindowInsets(),
                    containerColor = LocalContainerColor.current
                ) { contentPadding ->
                    val navigationState = LocalNavigationState.current
                    val scaffoldBound by remember(
                        navigator.scaffoldDirective.maxHorizontalPartitions,
                        navigator.scaffoldState.targetState.tertiary,
                        navigationState,
                        topBar
                    ) {
                        mutableStateOf(
                            CanonicalScaffoldBound(
                                start = navigationState is NavigationState.NavigationBar || !navigationState.visible,
                                top = topBar == null,
                                end = navigator.scaffoldDirective.maxHorizontalPartitions == 1 || navigator.scaffoldState.targetState.tertiary != PaneAdaptedValue.Expanded,
                                bottom = true
                            )
                        )
                    }
                    CompositionLocalProvider(LocalCanonicalScaffoldBound provides scaffoldBound) {
                        content(contentPadding + PaddingValues(bottom = fabHeight))
                    }
                }
            }
        }
    }
}

/**
 * 境界が必要な場合はtrue
 */
internal data class CanonicalScaffoldBound(
    val start: Boolean = false,
    val top: Boolean = false,
    val end: Boolean = false,
    val bottom: Boolean = false,
)

internal val LocalCanonicalScaffoldBound = compositionLocalOf { CanonicalScaffoldBound() }

private val FabSpacing = 16.dp

@PreviewMultiScreen
@Composable
private fun CanonicalScaffoldPreview(
    @PreviewParameter(CanonicalScaffoldPreviewParameterProvider::class) item: CanonicalScaffoldPreviewParameter,
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

private data class CanonicalScaffoldPreviewParameter(
    val topAppBar: Boolean,
    val showNavigation: Boolean,
    val systemBar: Boolean,
    val destinationItem: ThreePaneScaffoldDestinationItem<String>,
)

private class CanonicalScaffoldPreviewParameterProvider :
    PreviewParameterProvider<CanonicalScaffoldPreviewParameter> {

    override val values: Sequence<CanonicalScaffoldPreviewParameter> =
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
                        CanonicalScaffoldPreviewParameter(
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
