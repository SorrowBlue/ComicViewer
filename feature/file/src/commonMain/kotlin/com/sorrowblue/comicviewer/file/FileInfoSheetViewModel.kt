package com.sorrowblue.comicviewer.file

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFolderBookThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.ExistsReadlaterUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class FileInfoSheetViewModel(
    val getFileAttributeUseCase: GetFileAttributeUseCase,
    val existsReadlaterUseCase: ExistsReadlaterUseCase,
    val deleteReadLaterUseCase: DeleteReadLaterUseCase,
    val addReadLaterUseCase: AddReadLaterUseCase,
    private val pagingFolderBookThumbnailsUseCase: PagingFolderBookThumbnailsUseCase,
) : ViewModel() {

    fun pagingDataFlow(bookshelfId: BookshelfId, path: String) =
        if (this.bookshelfId != bookshelfId || this.path != path) {
            this.bookshelfId = bookshelfId
            this.path = path
            internalPagingDataFlow(bookshelfId, path)
        } else {
            pagingDataFlow ?: internalPagingDataFlow(bookshelfId, path)
        }

    private var pagingDataFlow: Flow<PagingData<BookThumbnail>>? = null
    private var bookshelfId: BookshelfId? = null
    private var path: String? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun internalPagingDataFlow(bookshelfId: BookshelfId, path: String) =
        pagingFolderBookThumbnailsUseCase(
            PagingFolderBookThumbnailsUseCase.Request(bookshelfId, path, PagingConfig(40))
        ).filterSuccess().flattenConcat().cachedIn(viewModelScope).also { pagingDataFlow = it }
}

fun <T, E : Resource.AppError> Flow<Resource<T, E>>.filterSuccess(): Flow<T> {
    return filter {
        it is Resource.Success<T>
    }.map {
        (it as Resource.Success<T>).data
    }
}
