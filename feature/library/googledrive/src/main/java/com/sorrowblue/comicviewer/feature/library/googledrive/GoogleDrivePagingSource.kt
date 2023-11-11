package com.sorrowblue.comicviewer.feature.library.googledrive

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.api.services.drive.Drive
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class GoogleDrivePagingSource(
    private val driverService: Drive,
    private val parent: String,
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
            val request = driverService.files().list()
                .setQ(
                    "'$parent' in parents and trashed = false and (mimeType = 'application/vnd.google-apps.folder' or mimeType contains 'zip' or mimeType contains 'pdf')"
                )
                .setSpaces("drive")
                .setPageSize(params.loadSize)
                .setPageToken(params.key)
                .setFields("nextPageToken,files(id,name,parents,modifiedTime,size,mimeType,iconLink)")
            val fileList = request.execute()
            val list = fileList.files?.map {
                if (it.mimeType == "application/vnd.google-apps.folder") {
                    Folder(
                        BookshelfId(0),
                        it.name,
                        it.parents.joinToString(","),
                        it.id,
                        0,
                        it.modifiedTime.value,
                        mapOf("iconLink" to it.iconLink)
                    )
                } else {
                    BookFile(
                        BookshelfId(0),
                        it.name,
                        it.parents.joinToString(","),
                        it.id,
                        kotlin.runCatching { it.getSize() }.getOrElse { 0 },
                        it.modifiedTime.value,
                        "",
                        0,
                        0,
                        0,
                        mapOf("iconLink" to it.iconLink)
                    )
                }
            }
            LoadResult.Page(
                data = list.orEmpty(),
                prevKey = null, // Only paging forward.
                nextKey = fileList.nextPageToken
            )
        }
    }
}
