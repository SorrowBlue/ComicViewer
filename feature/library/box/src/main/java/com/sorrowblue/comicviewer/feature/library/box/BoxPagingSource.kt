package com.sorrowblue.comicviewer.feature.library.box

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.box.data.BoxApiRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class BoxPagingSource(
    private val parent: String,
    private val repository: BoxApiRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PagingSource<Int, File>() {

    override fun getRefreshKey(state: PagingState<Int, File>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)
        }?.nextKey
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, File> {
        val result = withContext(dispatcher) {
            repository.list(parent, params.loadSize.toLong(), params.key?.toLong() ?: 0)
        }
        val list = result?.mapNotNull {
            when (it.type) {
                "folder" -> {
                    Folder(
                        BookshelfId(0),
                        it.name,
                        parent,
                        it.id,
                        0,
                        0,
                    )
                }

                "file" -> {
                    BookFile(
                        BookshelfId(0),
                        it.name,
                        parent,
                        it.id,
                        it.size,
                        0,
                        "",
                        0,
                        0,
                        0,
                        mapOf(
                            "thumbnail" to repository.fileThumbnail(it.id).orEmpty(),
                            "access_token" to repository.accessToken()
                        )
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
