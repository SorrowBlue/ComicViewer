package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal sealed interface FavoriteAddScreenStateEvent {
    class AddClick(val bookshelfId: BookshelfId, val path: String) : FavoriteAddScreenStateEvent
}

internal interface FavoriteAddScreenState {
    val pagingDataFlow: Flow<PagingData<Favorite>>
    val recentFavoritesFlow: Flow<PagingData<Favorite>>
    val events: EventFlow<FavoriteAddScreenStateEvent>
    fun onFavoriteClick(favorite: Favorite)
    fun onNewFavoriteClick()
}

@Composable
internal fun rememberFavoriteAddScreenState(
    route: FavoriteAdd,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FavoriteAddViewModel = koinViewModel { parametersOf(route) },
): FavoriteAddScreenState = remember {
    FavoriteAddScreenStateImpl(
        route = route,
        scope = scope,
        addFavoriteFileUseCase = viewModel.addFavoriteFileUseCase,
        removeFavoriteFileUseCase = viewModel.removeFavoriteFileUseCase,
        pagingDataFlow = viewModel.pagingDataFlow,
        recentFavoritesFlow = viewModel.recentPagingDataFlow
    )
}

private class FavoriteAddScreenStateImpl(
    private val route: FavoriteAdd,
    private val scope: CoroutineScope,
    private val addFavoriteFileUseCase: AddFavoriteFileUseCase,
    private val removeFavoriteFileUseCase: RemoveFavoriteFileUseCase,
    override val pagingDataFlow: Flow<PagingData<Favorite>>,
    override val recentFavoritesFlow: Flow<PagingData<Favorite>>,
) : FavoriteAddScreenState {

    override val events = EventFlow<FavoriteAddScreenStateEvent>()

    override fun onFavoriteClick(favorite: Favorite) {
        scope.launch {
            if (favorite.exist) {
                removeFavoriteFileUseCase(
                    RemoveFavoriteFileUseCase.Request(
                        FavoriteFile(favorite.id, route.bookshelfId, route.path)
                    )
                ).first().fold(
                    onSuccess = {},
                    onError = {}
                )
            } else {
                addFavoriteFileUseCase(
                    AddFavoriteFileUseCase.Request(
                        FavoriteFile(favorite.id, route.bookshelfId, route.path)
                    )
                ).collect()
            }
        }
    }

    override fun onNewFavoriteClick() {
        events.tryEmit(FavoriteAddScreenStateEvent.AddClick(route.bookshelfId, route.path))
    }
}
