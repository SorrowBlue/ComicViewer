package com.sorrowblue.comicviewer.library.dropbox.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.BookFile
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.library.dropbox.data.DropBoxApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class DropBoxPagingSource(
    private val parent: String,
    private val repository: DropBoxApiRepository,
) :
    PagingSource<String, File>() {
    override fun getRefreshKey(state: PagingState<String, File>): String? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)
        }?.nextKey
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, File> {
        val result = withContext(Dispatchers.IO) {
            repository.list(parent, params.loadSize.toLong(), params.key)
        }
        val list = result?.entries?.mapNotNull {
            when (it) {
                is FolderMetadata -> {
                    Folder(
                        BookshelfId(0),
                        it.name,
                        parent,
                        it.pathLower,
                        0,
                        0,
                        mapOf("preview_url" to it.previewUrl)
                    )
                }

                is FileMetadata -> {
                    BookFile(
                        BookshelfId(0),
                        it.name,
                        parent,
                        it.pathLower,
                        it.size,
                        it.serverModified.time,
                        "",
                        0,
                        0,
                        0,
                        mapOf("preview_url" to it.previewUrl)
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
