package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.component.BookshelfAppBar
import com.sorrowblue.comicviewer.feature.bookshelf.component.BookshelfFab
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDelete
import com.sorrowblue.comicviewer.feature.bookshelf.section.BookshelfSheet
import com.sorrowblue.comicviewer.framework.annotation.Destination
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NestedNavGraph
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.koin.compose.currentKoinScope
import org.koin.compose.koinInject
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Serializable
data object BookshelfNav

@Serializable
data class TypedBookshelf(
    val bookshelfId: BookshelfId,
    val s: String,
    val sl: List<String>,
    val sa: Array<String>,
    val i: Int,
    val il: List<Int>,
    val ia: IntArray,
    val l: Long,
    val ll: List<Long>,
    val la: LongArray,
    val f: Float,
    val fl: List<Float>,
    val fa: FloatArray,
    val b: Boolean,
    val bl: List<Boolean>,
    val ba: BooleanArray,
)

@Factory
class BookshelfNavGraphNavigator(val navController: NavController) : BookshelfScreenNavigator {
    override fun onSettingsClick() {
        TODO("Not yet implemented")
    }

    override fun onFabClick() {
        TODO("Not yet implemented")
    }

    override fun onBookshelfClick(bookshelfId: BookshelfId, path: String) {
        TODO("Not yet implemented")
    }
}

interface BookshelfScreenNavigator {
    fun onSettingsClick()
    fun onFabClick()
    fun onBookshelfClick(bookshelfId: BookshelfId, path: String)
}

@com.sorrowblue.comicviewer.framework.annotation.NavGraph<BookshelfNav>(
    startDestination = Bookshelf2::class,
)
class BookshelfNavigation {
    @DestinationInGraph<Bookshelf2>
    @DestinationInGraph<BookshelfDelete>
    companion object
}

@Serializable
data object Bookshelf2

@Destination<Bookshelf2>
@Composable
fun BookshelfScreen2() {
    BookshelfScreen()
}

@Destination<TypedBookshelf>()
@Composable
fun BookshelfScreen(
) {
    val navigator: BookshelfScreenNavigator = koinInject(scope = currentKoinScope())
    val state: BookshelfScreenState = rememberBookshelfScreenState()
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
//        BookshelfInfoSheet(
//            bookshelfId = contentKey,
//            onCloseClick = state::onSheetCloseClick,
//            navigator = navigator,
//            snackbarHostState = state.snackbarHostState,
//        )
    }

//    NavTabHandler(onClick = state::onNavClick)
}

@Composable
internal fun BookshelfScreen(
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
        BookshelfSheet(
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
