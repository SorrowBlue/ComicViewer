package com.sorrowblue.comicviewer.bookshelf

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.wrapper.DestinationWrapper
import com.sorrowblue.comicviewer.bookshelf.component.BookshelfFab
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfGraph
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfAppBar
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfMainSheet
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoSheet
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoSheetNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.info.LocalDestinationScopeWithNoDependencies
import com.sorrowblue.comicviewer.feature.bookshelf.info.LocalSnackbarHostState
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeInternalStorage
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowEmptyData
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowLoadingData
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCompliantNavigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

internal interface BookshelfScreenNavigator : BookshelfInfoSheetNavigator {
    fun onSettingsClick()
    fun onFabClick()
    fun onBookshelfClick(bookshelfId: BookshelfId, path: String)
    fun onEditClick(bookshelfId: BookshelfId)
}

object BookshelfScreenWrapper : DestinationWrapper {
    @Composable
    override fun <T> DestinationScope<T>.Wrap(
        @SuppressLint(
            "ComposableLambdaParameterNaming"
        ) screenContent: @Composable () -> Unit,
    ) {
        CompositionLocalProvider(LocalDestinationScopeWithNoDependencies provides this) {
            screenContent()
        }
    }
}

@Destination<BookshelfGraph>(
    start = true,
    wrappers = [BookshelfScreenWrapper::class],
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun BookshelfScreen(
    navigator: BookshelfScreenNavigator,
    state: BookshelfScreenState = rememberBookshelfScreenState(),
) {
    BookshelfScreen(
        navigator = state.navigator,
        lazyPagingItems = state.pagingItems,
        lazyGridState = state.lazyGridState,
        snackbarHostState = state.snackbarHostState,
        onFabClick = navigator::onFabClick,
        onSettingsClick = navigator::onSettingsClick,
        onBookshelfClick = navigator::onBookshelfClick,
        onBookshelfInfoClick = state::onBookshelfInfoClick,
    ) { contentKey ->
        CompositionLocalProvider(LocalSnackbarHostState provides state.snackbarHostState) {
            val currentNavigator = remember(navigator) {
                object : BookshelfInfoSheetNavigator {
                    override fun notificationRequest() = navigator.notificationRequest()

                    override fun edit(id: BookshelfId) = navigator.edit(id)

                    override fun remove(bookshelfId: BookshelfId) = navigator.remove(bookshelfId)

                    override fun navigateBack() {
                        state.back()
                    }
                }
            }
            BookshelfInfoSheet(
                bookshelfId = contentKey,
                navigator = currentNavigator,
            )
        }
    }

    NavTabHandler(onClick = state::onNavClick)
}

@Composable
private fun BookshelfScreen(
    navigator: ThreePaneScaffoldNavigator<BookshelfId>,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    snackbarHostState: SnackbarHostState,
    onFabClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    extraPane: @Composable (BookshelfId) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val expanded by rememberLastScrolledForward(lazyGridState, 300)
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            BookshelfAppBar(
                onSettingsClick = onSettingsClick,
                scrollBehavior = scrollBehavior,
                scrollableState = lazyGridState
            )
        },
        floatingActionButton = { BookshelfFab(expanded = expanded, onClick = onFabClick) },
        extraPane = extraPane,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        BookshelfMainSheet(
            lazyPagingItems = lazyPagingItems,
            lazyGridState = lazyGridState,
            onBookshelfClick = onBookshelfClick,
            onBookshelfInfoClick = onBookshelfInfoClick,
            contentPadding = contentPadding
        )
    }
}

@Composable
private fun rememberLastScrolledForward(lazyGridState: LazyGridState, delay: Long): State<Boolean> {
    val expanded = remember { mutableStateOf(true) }
    LaunchedEffect(lazyGridState.lastScrolledForward) {
        delay(delay)
        expanded.value = !lazyGridState.lastScrolledForward
    }
    return expanded
}

@PreviewMultiScreen
@Composable
private fun PreviewBookshelfScreen(
    @PreviewParameter(PagingDataProvider::class) pagingDataFlow: Flow<PagingData<BookshelfFolder>>,
) {
    PreviewCompliantNavigation {
        val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
        val lazyGridState = rememberLazyGridState()
        BookshelfScreen(
            navigator = rememberSupportingPaneScaffoldNavigator<BookshelfId>(),
            lazyPagingItems = lazyPagingItems,
            snackbarHostState = remember { SnackbarHostState() },
            onFabClick = {},
            onSettingsClick = {},
            onBookshelfClick = { _, _ -> },
            onBookshelfInfoClick = {},
            lazyGridState = lazyGridState
        ) { _ -> }
    }
}

private class PagingDataProvider : PreviewParameterProvider<Flow<PagingData<BookshelfFolder>>> {
    override val values: Sequence<Flow<PagingData<BookshelfFolder>>> = sequenceOf(
        PagingData.flowData { BookshelfFolder(fakeInternalStorage(it), fakeFolder()) },
        PagingData.flowLoadingData(),
        PagingData.flowEmptyData()
    )
}
