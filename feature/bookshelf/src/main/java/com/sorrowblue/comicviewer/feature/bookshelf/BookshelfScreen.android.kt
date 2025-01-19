package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.paging.PagingData
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfGraph
import com.sorrowblue.comicviewer.framework.annotation.DestinationImpl
import com.sorrowblue.comicviewer.framework.navigation.DestinationStyle
import com.sorrowblue.comicviewer.framework.navigation.NavGraph
import com.sorrowblue.comicviewer.framework.navigation.ScreenDestination
import com.sorrowblue.comicviewer.framework.ui.navigation.DefaultDestinationScopeWrapper
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeInternalStorage
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowEmptyData
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowLoadingData
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCompliantNavigation
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Singleton

/*
@DestinationImpl
@Singleton
class BookshelfScreenRoute : ScreenDestination {
    override val route = Bookshelf::class

    override val typeMap: Map<KType, NavType<*>> = emptyMap()
    override val style: DestinationStyle = DestinationStyle.Composable

    @Composable
    override fun NavBackStackEntry.Content() {
        BookshelfScreen(
            navigator = object : BookshelfScreenNavigator {
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
        )
    }
}


@Singleton(binds = [NavGraph::class])
class BookshelfNavGraph2 : NavGraph {
    override val startDestination: KClass<*> = Bookshelf::class
    override val route: KClass<*> = BookshelfNav::class
    override val typeMap: Map<KType, NavType<*>> = emptyMap()
    override val screenDestinations: List<ScreenDestination> = listOf(BookshelfScreenRoute())
    override val nestedNavGraphs: List<NavGraph> = emptyList()
}
*/

@Destination<BookshelfGraph>(
    start = true,
    wrappers = [DefaultDestinationScopeWrapper::class],
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun BookshelfScreen(
    navigator: BookshelfScreenNavigator,
) {
    BookshelfScreen(
        navigator = navigator,
    )
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
