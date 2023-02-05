package com.sorrowblue.comicviewer.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.common.util.SortUtil
import com.sorrowblue.comicviewer.data.database.FileModelRemoteMediator
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.di.IoDispatcher
import com.sorrowblue.comicviewer.data.exception.RemoteException
import com.sorrowblue.comicviewer.domain.PagingException
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
internal class FileModelRemoteMediatorImpl @AssistedInject constructor(
    remoteDataSourceFactory: RemoteDataSource.Factory,
    settingsCommonRepository: SettingsCommonRepository,
    @Assisted private val serverModel: ServerModel,
    @Assisted private val fileModel: FileModel,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
) : FileModelRemoteMediator() {

    @AssistedFactory
    interface Factory : FileModelRemoteMediator.Factory {
        override fun create(
            serverModel: ServerModel,
            fileModel: FileModel,
        ): FileModelRemoteMediatorImpl
    }

    private val folderSettings = settingsCommonRepository.folderSettings
    private val remoteDataSource = remoteDataSourceFactory.create(serverModel)

    override suspend fun initialize(): InitializeAction {
        return if (folderSettings.first().isAutoRefresh) InitializeAction.LAUNCH_INITIAL_REFRESH else InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, File>,
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
                fileModelLocalDataSource.withTransaction {

                    // リモートになくてDBにある項目：削除対象
                    val deleteFileData = fileModelLocalDataSource.selectByNotPaths(
                        fileModel.serverModelId,
                        fileModel.path,
                        files.map(FileModel::path)
                    )
                    // DBから削除
                    fileModelLocalDataSource.deleteAll(deleteFileData)

                    // existsFiles DBにある項目：更新対象
                    // noExistsFiles DBにない項目：挿入対象
                    val (existsFiles, noExistsFiles) = files.partition {
                        fileModelLocalDataSource.exists(it.serverModelId, it.path)
                    }

                    // DBにない項目を挿入
                    fileModelLocalDataSource.registerAll(noExistsFiles)

                    // DBにファイルを更新
                    // ファイルサイズ、更新日時、タイプ ソート、インデックス
                    fileModelLocalDataSource.updateAll(existsFiles.map(FileModel::simple))
                }
            }
        }.fold({
            return MediatorResult.Success(endOfPaginationReached = true)
        }, {
            val error = if (it is RemoteException) {
                when (it)  {
                    RemoteException.InvalidAuth -> PagingException.InvalidAuth
                    RemoteException.InvalidServer -> PagingException.InvalidServer
                    RemoteException.NoNetwork -> PagingException.NoNetwork
                    RemoteException.NotFound -> PagingException.NotFound
                }
            } else {
                it
            }
            return MediatorResult.Error(error)
        })
    }
}
