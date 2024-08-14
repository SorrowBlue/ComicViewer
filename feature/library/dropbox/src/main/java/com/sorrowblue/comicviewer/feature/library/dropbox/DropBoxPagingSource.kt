package com.sorrowblue.comicviewer.feature.library.dropbox

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.dropbox.data.DropBoxApiRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class DropBoxPagingSource(
    private val parent: String,
    private val repository: DropBoxApiRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PagingSource<String, File>() {

    override fun getRefreshKey(state: PagingState<String, File>): String? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)
        }?.nextKey
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, File> {
        val result = withContext(dispatcher) {
            repository.list(parent, params.loadSize.toLong(), params.key)
        }
        val list = result?.entries?.mapNotNull {
            when (it) {
                is FolderMetadata -> {
                    Folder(
                        BookshelfId(),
                        it.name,
                        parent,
                        it.pathLower.orEmpty(),
                        0,
                        0,
                        false,
                    )
                }

                is FileMetadata -> {
                    BookFile(
                        BookshelfId(),
                        it.name,
                        parent,
                        it.pathLower.orEmpty(),
                        it.size,
                        it.serverModified.time,
                        false,
                        "",
                        0,
                        0,
                        0,
                    )
                }

                else -> null
            }
        }.orEmpty()
        return LoadResult.Page(
            data = list,
            prevKey = null,
            nextKey = if (result?.hasMore == true) result.cursor else null
        )
    }
}
