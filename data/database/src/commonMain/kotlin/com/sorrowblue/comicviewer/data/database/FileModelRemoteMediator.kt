package com.sorrowblue.comicviewer.data.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.sorrowblue.comicviewer.data.database.entity.file.QueryFileWithCountEntity
import com.sorrowblue.comicviewer.domain.model.PagingException
import com.sorrowblue.comicviewer.domain.model.SortUtil
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteException
import di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import logcat.logcat
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Singleton
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

@Singleton(binds = [FileModelRemoteMediator.Factory::class])
internal class FileModelRemoteMediatorFactory :
    FileModelRemoteMediator.Factory, KoinComponent {
    override fun create(bookshelf: Bookshelf, file: File): FileModelRemoteMediator {
        return get<FileModelRemoteMediator> { parametersOf(bookshelf, file) }
    }
}

@OptIn(ExperimentalPagingApi::class)
@Factory
internal class FileModelRemoteMediator(
    remoteDataSourceFactory: RemoteDataSource.Factory,
    datastoreDataSource: DatastoreDataSource,
    @InjectedParam private val bookshelf: Bookshelf,
    @InjectedParam private val file: File,
    @Qualifier(IoDispatcher::class) private val dispatcher: CoroutineDispatcher,
    private val fileLocalDataSource: FileLocalDataSource,
) : RemoteMediator<Int, QueryFileWithCountEntity>() {

    interface Factory {
        fun create(bookshelf: Bookshelf, file: File): FileModelRemoteMediator
    }

    private val folderSettings = datastoreDataSource.folderSettings
    private val remoteDataSource = remoteDataSourceFactory.create(bookshelf)

    override suspend fun initialize() = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, QueryFileWithCountEntity>,
    ): MediatorResult {
        if (loadType != LoadType.REFRESH) {
            return MediatorResult.Success(endOfPaginationReached = true)
        }
        logcat { "load $loadType" }
        kotlin.runCatching {
            withContext(dispatcher) {
                val settings = folderSettings.first()
                val supportExtensions = settings.supportExtension.map(SupportExtension::extension)
                val files = SortUtil.sortedIndex(
                    remoteDataSource.listFiles(file, settings.resolveImageFolder) {
                        SortUtil.filter(it, supportExtensions)
                    }
                )
                fileLocalDataSource.updateHistory(file, files)
            }
        }.fold({
            return MediatorResult.Success(endOfPaginationReached = true)
        }, {
            val error = if (it is RemoteException) {
                when (it) {
                    is RemoteException.InvalidAuth -> PagingException.InvalidAuth()
                    is RemoteException.InvalidServer -> PagingException.InvalidServer()
                    is RemoteException.NoNetwork -> PagingException.NoNetwork()
                    is RemoteException.NotFound -> PagingException.NotFound()
                    is RemoteException.Unknown -> PagingException.NotFound()
                }
            } else {
                it
            }
            return MediatorResult.Error(error)
        })
    }
}
