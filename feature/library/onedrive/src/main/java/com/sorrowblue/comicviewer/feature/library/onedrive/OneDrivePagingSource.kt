package com.sorrowblue.comicviewer.feature.library.onedrive

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.microsoft.graph.requests.DriveItemCollectionPage
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.onedrive.data.OneDriveApiRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.logcat

internal class OneDrivePagingSource(
    val driveId: String?,
    private val itemId: String,
    private val repository: OneDriveApiRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PagingSource<String, File>() {

    override fun getRefreshKey(state: PagingState<String, File>): String? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)
        }?.nextKey
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, File> {
        logcat { "driveId=$driveId, itemId=$itemId" }
        return withContext(dispatcher) {
            val collectionPage = repository.list(driveId, itemId, params.loadSize, params.key)
            val driveId = driveId ?: repository.driveId()
            logcat { "driveId=$driveId" }
            val list = collectionPage.currentPage.mapNotNull {
                if (it.folder != null) {
                    Folder(
                        BookshelfId(0),
                        it.name.orEmpty(),
                        itemId,
                        it.id.orEmpty(),
                        it.size ?: 0,
                        it.lastModifiedDateTime?.toEpochSecond() ?: 0,
                        mapOf(
                            "driveId" to driveId,
                            "thumbnail" to it.thumbnails?.currentPage?.firstOrNull()?.medium?.url.orEmpty()
                        )
                    )
                } else if (it.file != null) {
                    BookFile(
                        BookshelfId(0),
                        it.name.orEmpty(),
                        itemId,
                        it.id.orEmpty(),
                        it.size ?: 0,
                        it.lastModifiedDateTime?.toEpochSecond() ?: 0,
                        "",
                        0,
                        0,
                        0,
                        mapOf(
                            "driveId" to driveId,
                            "thumbnail" to it.thumbnails?.currentPage?.firstOrNull()?.medium?.url.orEmpty()
                        )
                    )
                } else {
                    null
                }
            }
            LoadResult.Page(
                data = list,
                prevKey = null,
                nextKey = collectionPage.skiptoken
            )
        }
    }

    private val DriveItemCollectionPage.skiptoken: String?
        get() = nextPage?.buildRequest()?.options?.first {
            it.name == "\$skiptoken"
        }?.value?.toString()
}
