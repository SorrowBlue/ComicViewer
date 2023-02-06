package com.sorrowblue.comicviewer.data.reporitory

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ReadLaterFileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.datasource.ReadLaterFileModelLocalDataSource
import com.sorrowblue.comicviewer.data.toFile
import com.sorrowblue.comicviewer.data.toServerId
import com.sorrowblue.comicviewer.domain.entity.ReadLaterFile
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.repository.ReadLaterRepository
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ReadLaterRepositoryImpl @Inject constructor(
    private val readLaterFileModelLocalDataSource: ReadLaterFileModelLocalDataSource
) : ReadLaterRepository {
    override suspend fun add(readLaterFile: ReadLaterFile): Result<ReadLaterFile, Unit> {
        return readLaterFileModelLocalDataSource.add(readLaterFile.toModel()).fold({
            Result.Success(it.toReadLater())
        }, {
            Result.Error(it)
        }, {
            Result.Exception(it)
        })
    }

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<File>> {
        return readLaterFileModelLocalDataSource.pagingDataFlow(pagingConfig)
            .map { it.map(FileModel::toFile) }
    }
}

fun ReadLaterFile.toModel() = ReadLaterFileModel(BookshelfModelId(bookshelfId.value), path)

fun ReadLaterFileModel.toReadLater() = ReadLaterFile(bookshelfModelId.toServerId(), path)
