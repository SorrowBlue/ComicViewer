package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.feature.favorite.add.destinations.FavoriteAddDialogScreenDestination
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal sealed interface FavoriteAddScreenStateEvent {
    class AddClick(val bookshelfId: BookshelfId, val path: String) : FavoriteAddScreenStateEvent
}

internal interface FavoriteAddScreenState : ScreenStateEvent<FavoriteAddScreenStateEvent> {
    val pagingDataFlow: Flow<PagingData<Favorite>>
    val recentFavoritesFlow: Flow<PagingData<Favorite>>
    fun onFavoriteClick(favorite: Favorite)
    fun onNewFavoriteClick()
}

@Composable
internal fun rememberFavoriteAddScreenState(
    navBackStackEntry: NavBackStackEntry,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FavoriteAddViewModel = hiltViewModel(),
): FavoriteAddScreenState = remember {
    FavoriteAddScreenStateImpl(
        args = FavoriteAddDialogScreenDestination.argsFrom(navBackStackEntry),
        scope = scope,
        viewModel = viewModel,
        addFavoriteFileUseCase = viewModel.addFavoriteFileUseCase,
        removeFavoriteFileUseCase = viewModel.removeFavoriteFileUseCase,
    )
}

private class FavoriteAddScreenStateImpl(
    viewModel: FavoriteAddViewModel,
    override val scope: CoroutineScope,
    private val args: FavoriteAddArgs,
    private val addFavoriteFileUseCase: AddFavoriteFileUseCase,
    private val removeFavoriteFileUseCase: RemoveFavoriteFileUseCase,
) : FavoriteAddScreenState {

    override val event = MutableSharedFlow<FavoriteAddScreenStateEvent>()

    override val pagingDataFlow = viewModel.pagingDataFlow

    override val recentFavoritesFlow = viewModel.recentPagingDataFlow

    override fun onFavoriteClick(favorite: Favorite) {
        scope.launch {
            if (favorite.exist) {
                removeFavoriteFileUseCase(
                    RemoveFavoriteFileUseCase.Request(
                        FavoriteFile(favorite.id, args.bookshelfId, args.path)
                    )
                ).first().fold(
                    onSuccess = {},
                    onError = {}
                )
            } else {
                addFavoriteFileUseCase(
                    AddFavoriteFileUseCase.Request(
                        FavoriteFile(favorite.id, args.bookshelfId, args.path)
                    )
                ).collect()
            }
        }
    }

    override fun onNewFavoriteClick() {
        sendEvent(FavoriteAddScreenStateEvent.AddClick(args.bookshelfId, args.path))
    }
}
