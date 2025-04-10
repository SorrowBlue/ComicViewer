package com.sorrowblue.comicviewer.feature.collection.add

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.paging.PagingData
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.ExperimentalIdValue
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.feature.collection.add.component.CollectionAddButton
import com.sorrowblue.comicviewer.feature.collection.add.section.BasicCollectionContent
import com.sorrowblue.comicviewer.feature.collection.add.section.CollectionAddAppBar
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Serializable
data class BasicCollectionAdd(
    val bookshelfId: BookshelfId,
    val path: String,
)

interface BasicCollectionAddNavigator {
    fun onCollectionCreateClick(bookshelfId: BookshelfId, path: String)
}

@Destination<BasicCollectionAdd>(style = DestinationStyle.Dialog::class)
@Composable
internal fun BasicCollectionAddScreen(
    route: BasicCollectionAdd,
    navController: NavController = koinInject(),
    navigator: BasicCollectionAddNavigator = koinInject(),
) {
    val state = rememberBasicCollectionAddScreenState(route)
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val recentFavorites = state.recentFavoritesFlow.collectAsLazyPagingItems()
    BasicCollectionAddScreen(
        lazyPagingItems = lazyPagingItems,
        recentFavorites = recentFavorites,
        onDismissRequest = navController::popBackStack,
        onClick = state::onCollectionClick,
        onCollectionCreateClick = {
            navigator.onCollectionCreateClick(route.bookshelfId, route.path)
        }
    )
}

@Composable
private fun BasicCollectionAddScreen(
    lazyPagingItems: LazyPagingItems<Pair<Collection, Boolean>>,
    recentFavorites: LazyPagingItems<Pair<Collection, Boolean>>,
    onDismissRequest: () -> Unit,
    onClick: (Collection, Boolean) -> Unit,
    onCollectionCreateClick: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(true),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0) },
        modifier = Modifier.statusBarsPadding()
    ) {
        Scaffold(
            topBar = { CollectionAddAppBar(onCloseClick = onDismissRequest) },
            floatingActionButton = { CollectionAddButton(onClick = onCollectionCreateClick) },
            contentWindowInsets = WindowInsets(0),
        ) { contentPadding ->
            BasicCollectionContent(
                recentLazyPagingItems = recentFavorites,
                lazyPagingItems = lazyPagingItems,
                contentPadding = contentPadding,
                onClick = { collection, exist ->
                    onClick(collection, exist)
                }
            )
        }
    }
}

@OptIn(ExperimentalIdValue::class)
@Preview
@Composable
private fun BasicCollectionAddScreenPreview() {
    PreviewTheme {
        BasicCollectionAddScreen(
            lazyPagingItems = PagingData.flowData {
                SmartCollection(
                    name = "Collection",
                    bookshelfId = BookshelfId(it * 2),
                    searchCondition = SearchCondition(),
                ).copy(CollectionId(it)) as Collection to true
            }.collectAsLazyPagingItems(),
            recentFavorites = PagingData.flowData {
                SmartCollection(
                    name = "Collection",
                    bookshelfId = BookshelfId(it * 3),
                    searchCondition = SearchCondition()
                ).copy(CollectionId(it)) as Collection to true
            }.collectAsLazyPagingItems(),
            onDismissRequest = {},
            onClick = { _, _ -> },
            onCollectionCreateClick = {}
        )
    }
}
