package com.sorrowblue.comicviewer.library.box.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.BookFile
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.library.box.data.BoxApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.logcat

internal class BoxPagingSource(
    private val parent: String?,
    private val repository: BoxApiRepository,
) :
    PagingSource<Int, File>() {
    override fun getRefreshKey(state: PagingState<Int, File>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)
        }?.nextKey
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, File> {
        val result = withContext(Dispatchers.IO) {
            repository.list(parent, params.loadSize.toLong(), params.key?.toLong() ?: 0)
        }
        val list = result?.mapNotNull {
            logcat { "${it.type},${it.name}" }
            when (it.type) {
                "folder" -> {
                    Folder(
                        BookshelfId(0),
                        it.name,
                        parent.orEmpty(),
                        it.id,
                        0,
                        0,
                    )
                }

                "file" -> {
                    BookFile(
                        BookshelfId(0),
                        it.name,
                        parent.orEmpty(),
                        it.id,
                        it.size,
                        it.modifiedAt.time,
                        "",
                        0,
                        0,
                        0,
                        mapOf("thumbnail" to repository.fileThumbnail(it.id).orEmpty(), "access_token" to repository.accessToken())
                    )
                }

                else -> null
            }
        }.orEmpty()
        return LoadResult.Page(
            data = list,
            prevKey = null,
            nextKey = null
        )
    }
}
