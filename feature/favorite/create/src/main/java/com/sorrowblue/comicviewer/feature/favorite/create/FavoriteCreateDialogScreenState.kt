package com.sorrowblue.comicviewer.feature.favorite.create

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
internal fun rememberFavoriteCreateDialogScreenState(
    navArgs: FavoriteCreateDialogScreenArgs,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FavoriteCreateDialogScreenViewModel = hiltViewModel(),
): FavoriteCreateDialogScreenState = rememberSaveableScreenState {
    FavoriteCreateDialogScreenStateImpl(
        savedStateHandle = it,
        navArgs = navArgs,
        context = context,
        scope = scope,
        createFavoriteUseCase = viewModel.createFavoriteUseCase,
        addFavoriteFileUseCase = viewModel.addFavoriteFileUseCase
    )
}

internal sealed interface FavoriteCreateDialogScreenEvent {
    data object Success : FavoriteCreateDialogScreenEvent
}

internal interface FavoriteCreateDialogScreenState :
    SaveableScreenState,
    ScreenStateEvent<FavoriteCreateDialogScreenEvent> {
    val uiState: FavoriteCreateDialogScreenUiState
    fun onSubmit(text: String)
}

@OptIn(SavedStateHandleSaveableApi::class)
private class FavoriteCreateDialogScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val scope: CoroutineScope,
    private val context: Context,
    private val navArgs: FavoriteCreateDialogScreenArgs,
    private val createFavoriteUseCase: CreateFavoriteUseCase,
    private val addFavoriteFileUseCase: AddFavoriteFileUseCase,
) : FavoriteCreateDialogScreenState {

    override val event = MutableSharedFlow<FavoriteCreateDialogScreenEvent>()
    override var uiState by savedStateHandle.saveable {
        mutableStateOf(FavoriteCreateDialogScreenUiState())
    }
        private set

    override fun onSubmit(text: String) {
        scope.launch {
            createFavoriteUseCase(CreateFavoriteUseCase.Request(text)).first()
                .fold({ data ->
                    navArgs.favoriteBooksToAdd?.let {
                        addFavoriteFile(data, it.bookshelfId, it.path)
                    } ?: run {
                        Toast.makeText(
                            context,
                            context.getString(
                                R.string.favorite_create_msg_success_create,
                                data.name
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                        sendEvent(FavoriteCreateDialogScreenEvent.Success)
                    }
                }, {
                    uiState = when (it) {
                        CreateFavoriteUseCase.Error.System -> uiState.copy(
                            error = context.getString(R.string.favorite_create_msg_error)
                        )

                        CreateFavoriteUseCase.Error.NotFound -> uiState.copy(
                            error = context.getString(R.string.favorite_create_msg_error)
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
                sendEvent(FavoriteCreateDialogScreenEvent.Success)
                Toast.makeText(
                    context,
                    context.getString(R.string.favorite_create_msg_success_add, favorite.name),
                    Toast.LENGTH_SHORT
                ).show()
            }, {
                uiState = when (it) {
                    AddFavoriteFileUseCase.Error.NotFound ->
                        uiState.copy(error = context.getString(R.string.favorite_create_msg_error))

                    AddFavoriteFileUseCase.Error.System ->
                        uiState.copy(error = context.getString(R.string.favorite_create_msg_error))
                }
            })
    }
}
