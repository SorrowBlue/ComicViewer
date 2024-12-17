package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
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
import com.sorrowblue.comicviewer.framework.designsystem.animation.extendFabAnimation
import com.sorrowblue.comicviewer.framework.designsystem.animation.topAppBarAnimation
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CompliantNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.preview.DeviceTemplate
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.scratch
import kotlinx.coroutines.launch

interface CanonicalScaffoldExtraPaneScope {
    val topAppBar: Boolean
    val contentPadding: PaddingValues
    val navigator: ThreePaneScaffoldNavigator<Any>
}

private data class DefaultCanonicalScaffoldExtraPaneScope(
    override val topAppBar: Boolean,
    override val contentPadding: PaddingValues,
    override val navigator: ThreePaneScaffoldNavigator<Any>,
) :
    CanonicalScaffoldExtraPaneScope

@Composable
fun <T : Any> CanonicalScaffold(
    navigator: ThreePaneScaffoldNavigator<T>,
    modifier: Modifier = Modifier,
    topBar: @Composable (() -> Unit)? = null,
    extraPane: @Composable (CanonicalScaffoldExtraPaneScope.(T) -> Unit)? = null,
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
        snackbarHost = snackbarHost,
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = ComicTheme.colorScheme.surface,
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
                        DefaultCanonicalScaffoldExtraPaneScope(
                            topAppBar = topBar != null,
                            contentPadding.asWindowInsets()
                                .exclude(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
                                .asPaddingValues(),
                            navigator
                        ).extraPane(contentKey!!)
                    }
                }
            }
        ) {
            if (navigator.scaffoldDirective.maxHorizontalPartitions != 1 && navigator.scaffoldValue.tertiary == PaneAdaptedValue.Expanded) {
                contentPadding.asWindowInsets()
                    .exclude(WindowInsets.safeDrawing.only(WindowInsetsSides.End))
                    .asPaddingValues()
            } else {
                contentPadding
            }.let { content(it) }
        }
    }
}

@Composable
private fun CanonicalScaffoldPreview(
    item: CanonicalScaffoldPreviewItem,
    destinationItem: ThreePaneScaffoldDestinationItem<String>,
) {
    PreviewTheme {
        val navigator = rememberSupportingPaneScaffoldNavigator(
            initialDestinationHistory = listOf(destinationItem)
        )
        val content = remember {
            movableContentOf {
                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                val scope = rememberCoroutineScope()
                CanonicalScaffold(
                    navigator = navigator,
                    topBar = if (item.topAppBar) {
                        {
                            CanonicalTopAppBar(
                                title = { Text("TopAppBar") },
                                scrollBehavior = scrollBehavior,
                                modifier = Modifier.scratch(Color.Red.copy(0.5f))
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
                                Modifier
                                    .fillMaxSize()
                                    .scratch(Color.Red.copy(0.5f))
                                    .padding(it)
                                    .scratch(Color.Blue.copy(0.5f))
                            )
                        }
                    },
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) { contentPadding ->
                    val pad by animatedMainContentPadding(
                        navigator = navigator,
                        topAppBar = item.topAppBar,
                        fillWidth = false
                    )
                    Box(
                        Modifier
                            .scratch(Color.Red.copy(0.5f))
                            .padding(contentPadding)
                            .scratch(Color.Blue.copy(0.5f))
                            .padding(pad)
                            .scratch(Color.Green.copy(0.5f))
                    )
                }
            }
        }
        if (item.showNavigation) {
            DeviceTemplate(item.template) {
                CompliantNavigationSuiteScaffold(
                    navigationSuiteItems = {
                        repeat(5) {
                            item(
                                selected = it == 2,
                                onClick = {},
                                icon = { Icon(ComicIcons.Image, null) }
                            )
                        }
                    },
                    content = content
                )
            }
        } else {
            DeviceTemplate(item.template) {
                CompositionLocalProvider(
                    value = LocalNavigationState provides when (
                        val navigationState =
                            LocalNavigationState.current
                    ) {
                        is NavigationState.NavigationBar -> navigationState.copy(visible = !navigationState.visible)
                        is NavigationState.NavigationDrawer -> navigationState.copy(visible = !navigationState.visible)
                        is NavigationState.NavigationRail -> navigationState.copy(visible = !navigationState.visible)
                    },
                    content = content
                )
            }
        }
    }
}

@PreviewMultiScreen
@Composable
private fun CanonicalScaffoldMainPanePreview(
    @PreviewParameter(CanonicalScaffoldPreviewItemProvider::class) item: CanonicalScaffoldPreviewItem,
) {
    CanonicalScaffoldPreview(
        item,
        ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main)
    )
}

@PreviewMultiScreen
@Composable
private fun CanonicalScaffoldExtraPanePreview(
    @PreviewParameter(CanonicalScaffoldPreviewItemProvider::class) item: CanonicalScaffoldPreviewItem,
) {
    CanonicalScaffoldPreview(
        item,
        ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "Extra pane preview")
    )
}

private data class CanonicalScaffoldPreviewItem(
    val topAppBar: Boolean,
    val showNavigation: Boolean,
    val template: DeviceTemplate,
)

private class CanonicalScaffoldPreviewItemProvider :
    PreviewParameterProvider<CanonicalScaffoldPreviewItem> {

    override val values = sequenceOf(
        CanonicalScaffoldPreviewItem(
            topAppBar = true,
            showNavigation = false,
            template = DeviceTemplate()
        ),
        CanonicalScaffoldPreviewItem(
            topAppBar = true,
            showNavigation = false,
            template = DeviceTemplate(isInvertedOrientation = true)
        ),
        CanonicalScaffoldPreviewItem(
            topAppBar = false,
            showNavigation = false,
            template = DeviceTemplate()
        ),
        CanonicalScaffoldPreviewItem(
            topAppBar = false,
            showNavigation = false,
            template = DeviceTemplate(isInvertedOrientation = true)
        ),
        CanonicalScaffoldPreviewItem(
            topAppBar = true,
            showNavigation = true,
            template = DeviceTemplate()
        ),
        CanonicalScaffoldPreviewItem(
            topAppBar = true,
            showNavigation = true,
            template = DeviceTemplate(isInvertedOrientation = true)
        ),
        CanonicalScaffoldPreviewItem(
            topAppBar = false,
            showNavigation = true,
            template = DeviceTemplate()
        ),
        CanonicalScaffoldPreviewItem(
            topAppBar = false,
            showNavigation = true,
            template = DeviceTemplate(isInvertedOrientation = true)
        ),
    )
}
