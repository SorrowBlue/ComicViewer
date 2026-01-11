package com.sorrowblue.comicviewer.feature.book

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.PluginManager
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn

internal interface BookScreenWrapperState {
    val uiState: BookScreenUiState
}

@Composable
context(context: BookScreenContext)
internal fun rememberBookScreenWrapperState(
    bookshelfId: BookshelfId,
    path: String,
    name: String,
    collectionId: CollectionId,
): BookScreenWrapperState {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        BookScreenWrapperStateImpl(
            bookshelfId = bookshelfId,
            path = path,
            name = name,
            collectionId = collectionId,
            coroutineScope = coroutineScope,
            getBookUseCase = context.getBookUseCase,
            pluginManager = context.pluginManager,
        )
    }
}

private class BookScreenWrapperStateImpl(
    bookshelfId: BookshelfId,
    path: String,
    name: String,
    collectionId: CollectionId,
    coroutineScope: CoroutineScope,
    getBookUseCase: GetBookUseCase,
    pluginManager: PluginManager,
) : BookScreenWrapperState {
    override var uiState: BookScreenUiState by mutableStateOf(BookScreenUiState.Loading(name))
        private set

    val error = callbackFlow {
        send("")
        val callback = object : PluginManager.Callback {
            override fun onError(msg: String) {
                trySend(msg)
            }
        }
        pluginManager.addCallback(callback)
        awaitClose {
            pluginManager.removeCallback(callback)
        }
    }

    init {
        getBookUseCase(GetBookUseCase.Request(bookshelfId, path)).combine(error) { res, error ->
            if (error.isNotEmpty()) {
                uiState = BookScreenUiState.PluginError(name, error)
            } else {
                uiState = when (res) {
                    is Resource.Success ->
                        BookScreenUiState.Loaded(
                            book = res.data,
                            collectionId = collectionId,
                            bookSheetUiState = BookSheetUiState(res.data),
                        )

                    is Resource.Error -> when (res.error) {
                        GetBookUseCase.Error.NotFound ->
                            BookScreenUiState.Error(name)

                        GetBookUseCase.Error.ReportedSystemError ->
                            BookScreenUiState.Error(name)
                    }
                }
            }
        }.launchIn(coroutineScope)
    }
}
