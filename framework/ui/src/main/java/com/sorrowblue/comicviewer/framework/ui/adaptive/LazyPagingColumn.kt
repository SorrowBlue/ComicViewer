package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewConfig
import com.sorrowblue.comicviewer.framework.ui.preview.layout.scratch
import com.sorrowblue.comicviewer.framework.ui.scrollbar.DraggableScrollbar
import com.sorrowblue.comicviewer.framework.ui.scrollbar.rememberDraggableScroller
import com.sorrowblue.comicviewer.framework.ui.scrollbar.scrollbarState

sealed class LazyPagingColumnType {

    abstract val columns: GridCells

    data object List : LazyPagingColumnType() {
        override val columns = GridCells.Fixed(1)
    }

    data object ListMedium : LazyPagingColumnType() {
        override val columns = GridCells.Adaptive(500.dp)
    }

    data class Grid(val minSize: Int) : LazyPagingColumnType() {
        override val columns = GridCells.Adaptive(minSize.dp)
    }
}

@Composable
fun <T : Any> LazyPagingColumn(
    lazyPagingItems: LazyPagingItems<T>,
    type: LazyPagingColumnType,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(),
    fillWidth: Boolean = type is LazyPagingColumnType.List,
    key: ((index: Int) -> Any)? = null,
    contentType: (index: Int) -> Any? = { type },
    itemContent: @Composable (LazyGridItemScope.(index: Int, item: T) -> Unit),
) {
    LazyVerticalGrid(
        columns = type.columns,
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = if (fillWidth) Arrangement.Top else Arrangement.spacedBy(ComicTheme.dimension.padding),
        horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.padding),
        modifier = modifier
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = key,
            contentType = contentType
        ) { index ->
            lazyPagingItems[index]?.let {
                itemContent(index, it)
            }
        }
    }
}

@PreviewMultiScreen
@Composable
private fun LazyPagingColumnPreview(
    @PreviewParameter(
        LazyPagingColumnConfigProvider::class
    ) config: LazyPagingColumnConfig,
) {
    val navigator = rememberSupportingPaneScaffoldNavigator(
        initialDestinationHistory = listOf(
            if (config.extra) {
                ThreePaneScaffoldDestinationItem(
                    SupportingPaneScaffoldRole.Extra,
                    "Extra"
                )
            } else {
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main)
            }
        )
    )
    PreviewCanonicalScaffold(
        config = PreviewConfig(isInvertedOrientation = true),
        navigator = navigator,
        extraPane = { contentKey ->
            ExtraPaneScaffold(
                title = { Text(contentKey) },
                onCloseClick = {},
            ) { }
        }
    ) {
        val type = when (LocalNavigationState.current) {
            is NavigationState.NavigationBar -> config.type
            is NavigationState.NavigationRail -> if (config.type is LazyPagingColumnType.List) LazyPagingColumnType.ListMedium else config.type
        }
        val fillWidth = type is LazyPagingColumnType.List
        val addPadding by animateMainContentPaddingValues(ignore = fillWidth)
        val lazyPagingItems = PagingData.flowData(35) { index -> "$index $index $index $index" }
            .collectAsLazyPagingItems()
        Box(modifier = Modifier.fillMaxSize()) {
            val state = rememberLazyGridState()
            LazyPagingColumn(
                state = state,
                lazyPagingItems = lazyPagingItems,
                type = type,
                contentPadding = it.add(addPadding),
                fillWidth = fillWidth,
                modifier = Modifier.fillMaxSize()
            ) { index, item ->
                when (type) {
                    is LazyPagingColumnType.List -> {
                        Column(modifier = Modifier.scratch(Color.Red)) {
                            ListItem(
                                headlineContent = { Text("List: $item") },
                                supportingContent = { Text("supportingContent") }
                            )
                            if (index < lazyPagingItems.itemCount - 1) {
                                HorizontalDivider()
                            }
                        }
                    }

                    is LazyPagingColumnType.ListMedium -> {
                        Card(modifier = Modifier.scratch(Color.Red)) {
                            ListItem(
                                headlineContent = { Text(item) },
                                supportingContent = { Text("supportingContent") },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }

                    is LazyPagingColumnType.Grid -> {
                        Card(modifier = Modifier.scratch(Color.Red)) {
                            Spacer(
                                Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(4f / 3f)
                                    .background(ComicTheme.colorScheme.surfaceDim)
                            )
                            Text(
                                item,
                                style = ComicTheme.typography.titleMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                modifier = Modifier.padding(ComicTheme.dimension.padding)
                            )
                        }
                    }
                }
            }
            val scrollbarState = state.scrollbarState(
                itemsAvailable = lazyPagingItems.itemCount,
            )
            state.DraggableScrollbar(
                modifier = Modifier
                    .fillMaxHeight()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 2.dp)
                    .align(Alignment.CenterEnd),
                state = scrollbarState,
                orientation = Orientation.Vertical,
                onThumbMove = state.rememberDraggableScroller(
                    itemsAvailable = lazyPagingItems.itemCount,
                ),
            )
        }
    }
}

private class LazyPagingColumnConfig(
    val type: LazyPagingColumnType,
    val extra: Boolean,
)

private class LazyPagingColumnConfigProvider : PreviewParameterProvider<LazyPagingColumnConfig> {
    override val values
        get() = sequenceOf(
            LazyPagingColumnConfig(LazyPagingColumnType.List, extra = false),
            LazyPagingColumnConfig(LazyPagingColumnType.List, extra = true),
            LazyPagingColumnConfig(LazyPagingColumnType.Grid(120), extra = false),
            LazyPagingColumnConfig(LazyPagingColumnType.Grid(120), extra = true),
        )
}
