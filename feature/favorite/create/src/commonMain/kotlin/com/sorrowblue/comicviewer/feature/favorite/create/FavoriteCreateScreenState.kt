package com.sorrowblue.comicviewer.feature.favorite.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.NotificationManager
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import comicviewer.feature.favorite.create.generated.resources.Res
import comicviewer.feature.favorite.create.generated.resources.favorite_create_msg_error
import comicviewer.feature.favorite.create.generated.resources.favorite_create_msg_success_add
import comicviewer.feature.favorite.create.generated.resources.favorite_create_msg_success_create
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun rememberFavoriteCreateScreenState(
    navArgs: FavoriteCreate,
    scope: CoroutineScope = rememberCoroutineScope(),
    notificationManager: NotificationManager = koinInject(),
    viewModel: FavoriteCreateScreenViewModel = koinViewModel(),
): FavoriteCreateScreenState = rememberSaveableScreenState {
    FavoriteCreateScreenStateImpl(
        savedStateHandle = it,
        scope = scope,
        route = navArgs,
        createFavoriteUseCase = viewModel.createFavoriteUseCase,
        addFavoriteFileUseCase = viewModel.addFavoriteFileUseCase,
        notificationManager = notificationManager,
    )
}

internal sealed interface FavoriteCreateScreenEvent {
    data object Success : FavoriteCreateScreenEvent
}

internal interface FavoriteCreateScreenState : SaveableScreenState {
    val uiState: FavoriteCreateScreenUiState
    val events: EventFlow<FavoriteCreateScreenEvent>
    fun onSubmit(text: String)
}

@OptIn(SavedStateHandleSaveableApi::class)
private class FavoriteCreateScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    private val scope: CoroutineScope,
    private val route: FavoriteCreate,
    private val createFavoriteUseCase: CreateFavoriteUseCase,
    private val addFavoriteFileUseCase: AddFavoriteFileUseCase,
    private val notificationManager: NotificationManager,
) : FavoriteCreateScreenState {

    override var uiState by savedStateHandle.saveable(
        stateSaver = Saver(
            save = { it.error },
            restore = { FavoriteCreateScreenUiState(it) }
        )
    ) {
        mutableStateOf(FavoriteCreateScreenUiState())
    }
        private set
    override val events = EventFlow<FavoriteCreateScreenEvent>()

    override fun onSubmit(text: String) {
        scope.launch {
            createFavoriteUseCase(CreateFavoriteUseCase.Request(text)).first()
                .fold({ data ->
                    if (route.bookshelfId != BookshelfId() && route.path.isNotEmpty()) {
                        addFavoriteFile(data, route.bookshelfId, route.path)
                    } else {
                        notificationManager.toast(
                            getString(
                                Res.string.favorite_create_msg_success_create,
                                data.name
                            ),
                            NotificationManager.LENGTH_SHORT
                        )
                        events.tryEmit(FavoriteCreateScreenEvent.Success)
                    }
                }, {
                    uiState = when (it) {
                        CreateFavoriteUseCase.Error.System -> uiState.copy(
                            error = getString(Res.string.favorite_create_msg_error)
                        )

                        CreateFavoriteUseCase.Error.NotFound -> uiState.copy(
                            error = getString(Res.string.favorite_create_msg_error)
                        )
                    }
                })
        }
    }

    private suspend fun addFavoriteFile(
        favorite: Favorite,
        bookshelfId: BookshelfId,
        path: String,
    ) {
        addFavoriteFileUseCase(
            AddFavoriteFileUseCase.Request(FavoriteFile(favorite.id, bookshelfId, path))
        ).first()
            .fold({
                events.tryEmit(FavoriteCreateScreenEvent.Success)
                notificationManager.toast(
                    getString(Res.string.favorite_create_msg_success_add, favorite.name),
                    NotificationManager.LENGTH_SHORT
                )
            }, {
                uiState = when (it) {
                    AddFavoriteFileUseCase.Error.NotFound ->
                        uiState.copy(error = getString(Res.string.favorite_create_msg_error))

                    AddFavoriteFileUseCase.Error.System ->
                        uiState.copy(error = getString(Res.string.favorite_create_msg_error))
                }
            })
    }
}
