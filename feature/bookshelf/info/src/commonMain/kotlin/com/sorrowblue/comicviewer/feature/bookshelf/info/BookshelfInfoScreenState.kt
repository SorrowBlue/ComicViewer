package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn

internal interface BookshelfInfoScreenState {
    val uiState: BookshelfInfoSheetUiState
}

@Composable
internal fun rememberBookshelfInfoScreenState(bookshelfId: BookshelfId): BookshelfInfoScreenState {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = assistedMetroViewModel<BookshelfInfoViewModel, BookshelfInfoViewModelFactory> {
        create(bookshelfId)
    }
    return remember(viewModel) {
        BookshelfInfoScreenStateImpl(
            viewModel = viewModel,
            coroutineScope = coroutineScope,
        )
    }
}

private class BookshelfInfoScreenStateImpl(
    viewModel: BookshelfInfoViewModel,
    coroutineScope: CoroutineScope,
) : BookshelfInfoScreenState {
    override var uiState by mutableStateOf<BookshelfInfoSheetUiState>(
        BookshelfInfoSheetUiState.Loading,
    )
        private set

    init {
        viewModel.bookshelfInfo.onEach {
            uiState = BookshelfInfoSheetUiState.Loaded(it)
        }.launchIn(coroutineScope)
    }
}

@AssistedInject
internal class BookshelfInfoViewModel(
    @Assisted bookshelfId: BookshelfId,
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
) : ViewModel() {

    val bookshelfInfo =
        getBookshelfInfoUseCase(
            GetBookshelfInfoUseCase.Request(bookshelfId = bookshelfId),
        ).mapNotNull { it.dataOrNull() }
            .shareIn(viewModelScope, started = SharingStarted.Eagerly)
}

@AssistedFactory
@ManualViewModelAssistedFactoryKey
@ContributesIntoMap(AppScope::class)
internal interface BookshelfInfoViewModelFactory : ManualViewModelAssistedFactory {
    fun create(bookshelfId: BookshelfId): BookshelfInfoViewModel
}
