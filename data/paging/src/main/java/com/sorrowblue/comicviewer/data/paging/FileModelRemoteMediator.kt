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
import com.sorrowblue.comicviewer.data.datasource.FileModelRemoteDataSource
import com.sorrowblue.comicviewer.data.di.IoDispatcher
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import logcat.logcat

@OptIn(ExperimentalPagingApi::class)
internal class FileModelRemoteMediatorImpl @AssistedInject constructor(
    fileModelRemoteDataSourceFactory: FileModelRemoteDataSource.Factory,
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

    private val bookShelfSettings = settingsCommonRepository.bookshelfSettings
    private val remoteDataSource = fileModelRemoteDataSourceFactory.create(serverModel)

    override suspend fun initialize(): InitializeAction {
        return if (bookShelfSettings.first().isAutoRefresh) InitializeAction.LAUNCH_INITIAL_REFRESH else InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, File>,
    ): MediatorResult {
        logcat { "loadType=$loadType, anchorPosition=${state.anchorPosition}" }
        kotlin.runCatching {
            withContext(dispatcher) {
                val settings = bookShelfSettings.first()
                val supportExtensions = settings.supportExtension.map(SupportExtension::extension)
                val files = SortUtil.sortedIndex(remoteDataSource.listFiles(fileModel, settings.resolveImageFolder) {
                    SortUtil.filter(it, supportExtensions)
                })
                fileModelLocalDataSource.withTransaction {

                    // ????????????????????????DB??????????????????????????????
                    val deleteFileData = fileModelLocalDataSource.selectByNotPaths(
                        fileModel.serverModelId,
                        fileModel.path,
                        files.map(FileModel::path)
                    )
                    // DB????????????
                    fileModelLocalDataSource.deleteAll(deleteFileData)

                    // existsFiles DB??????????????????????????????
                    // noExistsFiles DB??????????????????????????????
                    val (existsFiles, noExistsFiles) = files.partition {
                        fileModelLocalDataSource.exists(it.serverModelId, it.path)
                    }

                    // DB????????????????????????
                    fileModelLocalDataSource.registerAll(noExistsFiles)

                    // DB????????????????????????
                    // ???????????????????????????????????????????????? ??????????????????????????????
                    fileModelLocalDataSource.updateAll(existsFiles.map(FileModel::simple))
                }
            }
        }.fold({
            return MediatorResult.Success(endOfPaginationReached = true)
        }, {
            it.printStackTrace()
            return MediatorResult.Error(it)
        })
    }
}
