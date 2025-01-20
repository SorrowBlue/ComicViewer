package com.sorrowblue.comicviewer.feature.book

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import org.koin.compose.viewmodel.koinViewModel
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Stable
internal interface BookLoadingScreenState {
    val uiState: BookScreenUiState
}

@Composable
internal fun rememberBookLoadingScreenState(
    route: Book,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookViewModel = koinViewModel(),
): BookLoadingScreenState = remember {
    BookLoadingScreenStateImpl(
        route = route,
        scope = scope,
        viewModel = viewModel
    )
}

private class BookLoadingScreenStateImpl(
    route: Book,
    scope: CoroutineScope,
    viewModel: BookViewModel,
) : BookLoadingScreenState {

    override var uiState: BookScreenUiState by mutableStateOf(BookScreenUiState.Loading(route.name))
        private set

    init {
        viewModel.getBookUseCase(GetBookUseCase.Request(route.bookshelfId, route.path)).onEach {
            uiState = when (it) {
                is Resource.Success ->
                    BookScreenUiState.Loaded(
                        it.data,
                        route.favoriteId,
                        BookSheetUiState(it.data)
                    )

                is Resource.Error -> when (it.error) {
                    GetBookUseCase.Error.NotFound ->
                        BookScreenUiState.Error(route.name)

                    GetBookUseCase.Error.ReportedSystemError ->
                        BookScreenUiState.Error(route.name)
                }
            }
        }.launchIn(scope)
    }
}
