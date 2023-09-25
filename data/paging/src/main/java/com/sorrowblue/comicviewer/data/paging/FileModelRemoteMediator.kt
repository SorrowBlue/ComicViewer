package com.sorrowblue.comicviewer.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.common.util.SortUtil
import com.sorrowblue.comicviewer.data.database.FileModelRemoteMediator
import com.sorrowblue.comicviewer.data.database.entity.FileWithCount
import com.sorrowblue.comicviewer.data.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.di.IoDispatcher
import com.sorrowblue.comicviewer.data.exception.RemoteException
import com.sorrowblue.comicviewer.domain.model.PagingException
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
internal class FileModelRemoteMediatorImpl @AssistedInject constructor(
    remoteDataSourceFactory: RemoteDataSource.Factory,
    datastoreDataSource: DatastoreDataSource,
    @Assisted private val bookshelfModel: BookshelfModel,
    @Assisted private val fileModel: FileModel,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val fileModelLocalDataSource: FileModelLocalDataSource
) : FileModelRemoteMediator() {

    @AssistedFactory
    interface Factory : FileModelRemoteMediator.Factory {

        override fun create(
            bookshelfModel: BookshelfModel,
            fileModel: FileModel
        ): FileModelRemoteMediatorImpl
    }

    private val folderSettings = datastoreDataSource.folderSettings
    private val remoteDataSource = remoteDataSourceFactory.create(bookshelfModel)

    override suspend fun initialize() = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FileWithCount>
    ): MediatorResult {
        kotlin.runCatching {
            withContext(dispatcher) {
                val settings = folderSettings.first()
                val supportExtensions = settings.supportExtension.map(SupportExtension::extension)
                val files = SortUtil.sortedIndex(
                    remoteDataSource.listFiles(
                        fileModel,
                        settings.resolveImageFolder
                    ) {
                        SortUtil.filter(it, supportExtensions)
                    })
                fileModelLocalDataSource.updateHistory(fileModel, files)
            }
        }.fold({
            return MediatorResult.Success(endOfPaginationReached = true)
        }, {
            val error = if (it is RemoteException) {
                when (it) {
                    RemoteException.InvalidAuth -> PagingException.InvalidAuth
                    RemoteException.InvalidServer -> PagingException.InvalidServer
                    RemoteException.NoNetwork -> PagingException.NoNetwork
                    RemoteException.NotFound -> PagingException.NotFound
                    RemoteException.Unknown -> PagingException.NotFound
                }
            } else {
                it
            }
            return MediatorResult.Error(error)
        })
    }
}
