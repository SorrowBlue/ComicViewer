/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info.delete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.UpdateDeletionFlagUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal interface BookshelfDeleteScreenState {
    val uiState: BookshelfDeleteScreenUiState

    fun onConfirmClick(done: () -> Unit)
}

@Composable
internal fun rememberBookshelfDeleteScreenState(
    bookshelfId: BookshelfId,
): BookshelfDeleteScreenState {
    val coroutineScope = rememberCoroutineScope()
    val viewModel =
        assistedMetroViewModel<BookshelfDeleteViewModel, BookshelfDeleteViewModel.Factory> {
            create(bookshelfId)
        }
    return remember(viewModel) {
        BookshelfDeleteScreenStateImpl(
            scope = coroutineScope,
            viewModel = viewModel,
        )
    }
}

private class BookshelfDeleteScreenStateImpl(
    scope: CoroutineScope,
    private val viewModel: BookshelfDeleteViewModel,
) : BookshelfDeleteScreenState {
    override var uiState by mutableStateOf(BookshelfDeleteScreenUiState())
        private set

    init {
        viewModel.bookshelfFlow.onEach {
            uiState = uiState.copy(title = it.displayName)
        }.launchIn(scope)
    }

    override fun onConfirmClick(done: () -> Unit) {
        uiState = uiState.copy(isProcessing = true)
        viewModel.delete(
            done = {
                uiState = uiState.copy(isProcessing = false)
                done()
            },
            error = {
                uiState = uiState.copy(isProcessing = false)
            },
        )
    }
}

@AssistedInject
internal class BookshelfDeleteViewModel(
    @Assisted private val bookshelfId: BookshelfId,
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val updateDeletionFlagUseCase: UpdateDeletionFlagUseCase,
) : ViewModel() {

    val bookshelfFlow =
        getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId = bookshelfId))
            .mapNotNull { it.dataOrNull()?.bookshelf }

    fun delete(done: () -> Unit, error: () -> Unit) {
        viewModelScope.launch {
            when (
                updateDeletionFlagUseCase(
                    UpdateDeletionFlagUseCase.Request(bookshelfId, true),
                )
            ) {
                is Resource.Error -> {
                    error()
                }

                is Resource.Success -> {
                    done()
                }
            }
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(bookshelfId: BookshelfId): BookshelfDeleteViewModel
    }
}
