package com.sorrowblue.comicviewer.data.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.sorrowblue.comicviewer.data.database.entity.FileWithCountEntity
import com.sorrowblue.comicviewer.data.infrastructure.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.infrastructure.di.IoDispatcher
import com.sorrowblue.comicviewer.data.infrastructure.exception.RemoteException
import com.sorrowblue.comicviewer.domain.model.PagingException
import com.sorrowblue.comicviewer.domain.model.SortUtil
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.File
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
internal class FileModelRemoteMediator @AssistedInject constructor(
    remoteDataSourceFactory: RemoteDataSource.Factory,
    datastoreDataSource: DatastoreDataSource,
    @Assisted private val bookshelf: Bookshelf,
    @Assisted private val file: File,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
) : RemoteMediator<Int, FileWithCountEntity>() {

    @AssistedFactory
    interface Factory {

        fun create(bookshelf: Bookshelf, file: File): FileModelRemoteMediator
    }

    private val folderSettings = datastoreDataSource.folderSettings
    private val remoteDataSource = remoteDataSourceFactory.create(bookshelf)

    override suspend fun initialize() = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FileWithCountEntity>,
    ): MediatorResult {
        kotlin.runCatching {
            withContext(dispatcher) {
                val settings = folderSettings.first()
                val supportExtensions = settings.supportExtension.map(SupportExtension::extension)
                val files = SortUtil.sortedIndex(
                    remoteDataSource.listFiles(file, settings.resolveImageFolder) {
                        SortUtil.filter(it, supportExtensions)
                    }
                )
                fileModelLocalDataSource.updateHistory(file, files)
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
