package com.sorrowblue.comicviewer.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import org.koin.android.annotation.KoinViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map

@KoinViewModel
internal class FolderViewModel(
    val getFileUseCase: GetFileUseCase,
    val displaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val pagingFileUseCase: PagingFileUseCase,
) : ViewModel() {

    var pagingDataFlow: Flow<PagingData<File>>? = null

    var bookshelfId: BookshelfId? = null
    var path: String? = null

    fun pagingDataFlow(bookshelfId: BookshelfId, path: String) =
        if (this.bookshelfId != bookshelfId || this.path != path) {
            this.bookshelfId = bookshelfId
            this.path = path
            internalPagingDataFlow(bookshelfId, path)
        } else {
            pagingDataFlow ?: internalPagingDataFlow(bookshelfId, path)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun internalPagingDataFlow(bookshelfId: BookshelfId, path: String) =
        pagingFileUseCase(
            PagingFileUseCase.Request(PagingConfig(40), bookshelfId, path)
        ).filterSuccess().flattenConcat().cachedIn(viewModelScope).also { pagingDataFlow = it }
}

fun <T, E : Resource.AppError> Flow<Resource<T, E>>.filterSuccess(): Flow<T> {
    return filter {
        it is Resource.Success<T>
    }.map {
        (it as Resource.Success<T>).data
    }
}
