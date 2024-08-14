package com.sorrowblue.comicviewer.feature.library.googledrive

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.googledrive.data.GoogleDriveApiRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class GoogleDrivePagingSource(
    private val parent: String,
    private val repository: GoogleDriveApiRepository,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO,
) :
    PagingSource<String, File>() {
    override fun getRefreshKey(state: PagingState<String, File>): String? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)
        }?.nextKey
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, File> {
        return withContext(dispatchers) {
            val fileList = repository.fileList(parent, params.loadSize, params.key)
                ?: return@withContext LoadResult.Error(RuntimeException("DriveServiceが取得できませんでした"))
            val list = fileList.files?.map {
                if (it.mimeType == "application/vnd.google-apps.folder") {
                    Folder(
                        bookshelfId = BookshelfId(),
                        name = it.name,
                        parent = it.parents.joinToString(","),
                        path = it.id,
                        size = 0,
                        lastModifier = it.modifiedTime.value,
                        isHidden = false,
                        cacheKey = it.iconLink
                    )
                } else {
                    BookFile(
                        BookshelfId(),
                        it.name,
                        it.parents.joinToString(","),
                        it.id,
                        kotlin.runCatching { it.getSize() }.getOrElse { 0 },
                        it.modifiedTime.value,
                        false,
                        it.iconLink
                    )
                }
            }
            LoadResult.Page(
                data = list.orEmpty(),
                prevKey = null,
                nextKey = fileList.nextPageToken
            )
        }
    }
}
