/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.database

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.data.database.entity.file.QueryFileWithCountEntity
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.common.PagingException
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.SortUtil
import com.sorrowblue.comicviewer.domain.model.file.SupportExtension
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteException
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.logcat

@AssistedInject
internal class FileModelRemoteMediator(
    remoteDataSourceFactory: RemoteDataSource.Factory,
    settingsRepository: SettingsRepository,
    @Assisted private val bookshelf: Bookshelf,
    @Assisted private val file: File,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val dao: FileDao,
) : RemoteMediator<Int, QueryFileWithCountEntity>() {
    @AssistedFactory
    fun interface Factory {
        fun create(bookshelf: Bookshelf, file: File): FileModelRemoteMediator
    }

    private val folderSettings = settingsRepository.folderSettings
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
        kotlin
            .runCatching {
                withContext(dispatcher) {
                    val settings = folderSettings.first()
                    val supportExtensions = settings.supportExtension.map(
                        SupportExtension::extension,
                    )
                    val files = SortUtil.sortedIndex(
                        remoteDataSource.listFiles(file, false) {
                            SortUtil.filter(it, supportExtensions)
                        },
                    )
                    dao.updateSame(
                        FileEntity.fromModel(file),
                        files.map(FileEntity.Companion::fromModel),
                    )
                }
            }.fold({
                return MediatorResult.Success(endOfPaginationReached = true)
            }, {
                logcat(LogPriority.ERROR) { it.message.orEmpty() }
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
