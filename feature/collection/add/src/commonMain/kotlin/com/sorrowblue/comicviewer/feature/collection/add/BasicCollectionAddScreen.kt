package com.sorrowblue.comicviewer.feature.collection.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onLayoutRectChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.PagingData
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.feature.collection.add.component.CollectionSort
import com.sorrowblue.comicviewer.feature.collection.add.component.CollectionSortDropdownMenu
import com.sorrowblue.comicviewer.feature.collection.add.section.BasicCollectionContent
import com.sorrowblue.comicviewer.feature.collection.add.section.CollectionAddAppBar
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBasicCollection
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import comicviewer.feature.collection.add.generated.resources.Res
import comicviewer.feature.collection.add.generated.resources.collection_add_label_add
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Serializable
internal data class BasicCollectionAdd(val bookshelfId: BookshelfId, val path: String)

interface BasicCollectionAddNavigator {
    fun onCollectionCreateClick(bookshelfId: BookshelfId, path: String)
}

@Destination<BasicCollectionAdd>(style = DestinationStyle.Dialog::class)
@Composable
internal fun BasicCollectionAddScreen(
    route: BasicCollectionAdd,
    navController: NavController = koinInject(),
    navigator: BasicCollectionAddNavigator = koinInject(),
    state: BasicCollectionAddScreenState = rememberBasicCollectionAddScreenState(route),
) {
    BasicCollectionAddScreen(
        uiState = state.uiState,
        lazyPagingItems = state.lazyPagingItems,
        lazyListState = state.lazyListState,
        onDismissRequest = navController::popBackStack,
        onClick = state::onCollectionClick,
        onClickCollectionSort = state::onClickCollectionSort,
        onCollectionCreateClick = {
            navigator.onCollectionCreateClick(route.bookshelfId, route.path)
        }
    )
}

internal data class BasicCollectionAddScreenUiState(
    val collectionSort: CollectionSort = CollectionSort.Recent,
)

@Composable
private fun BasicCollectionAddScreen(
    uiState: BasicCollectionAddScreenUiState,
    lazyPagingItems: LazyPagingItems<Pair<Collection, Boolean>>,
    onDismissRequest: () -> Unit,
    onClick: (Collection, Boolean) -> Unit,
    onClickCollectionSort: (CollectionSort) -> Unit,
    onCollectionCreateClick: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
    sheetState: SheetState = rememberModalBottomSheetState(true),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0) },
        modifier = Modifier.statusBarsPadding()
    ) {
        var buttonHeight by remember { mutableStateOf(0.dp) }
        val density = LocalDensity.current
        Scaffold(
            topBar = { CollectionAddAppBar(onCloseClick = onDismissRequest) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = {
                        Text(text = stringResource(Res.string.collection_add_label_add))
                    },
                    icon = {
                        Icon(imageVector = ComicIcons.Add, contentDescription = null)
                    },
                    onClick = onCollectionCreateClick,
                    modifier = Modifier
                        .onLayoutRectChanged {
                            with(density) {
                                buttonHeight = it.height.toDp() + FabSpacing
                            }
                        }
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
        ) { contentPadding ->
            Box {
                BasicCollectionContent(
                    state = lazyListState,
                    lazyPagingItems = lazyPagingItems,
                    contentPadding = contentPadding
                        .plus(PaddingValues(top = ButtonDefaults.MinHeight, bottom = buttonHeight)),
                    onClick = onClick
                )
                CollectionSortDropdownMenu(
                    collectionSort = uiState.collectionSort,
                    onClick = onClickCollectionSort,
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(contentPadding.only(PaddingValuesSides.Top + PaddingValuesSides.End))
                )
            }
        }
    }
}

private val FabSpacing = 16.dp

@Preview
@Composable
private fun BasicCollectionAddScreenPreview() {
    PreviewTheme {
        BasicCollectionAddScreen(
            uiState = BasicCollectionAddScreenUiState(),
            lazyPagingItems = PagingData.flowData { fakeBasicCollection(it) as Collection to true }
                .collectAsLazyPagingItems(),
            onDismissRequest = {},
            onClick = { _, _ -> },
            onClickCollectionSort = { _ -> },
            onCollectionCreateClick = {},
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded,
                skipHiddenState = true
            )
        )
    }
}
