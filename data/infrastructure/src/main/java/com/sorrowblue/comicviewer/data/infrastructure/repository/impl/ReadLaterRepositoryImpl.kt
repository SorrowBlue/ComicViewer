package com.sorrowblue.comicviewer.data.infrastructure.repository.impl

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.infrastructure.datasource.ReadLaterFileModelLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.mapper.from
import com.sorrowblue.comicviewer.data.infrastructure.mapper.toBookshelfId
import com.sorrowblue.comicviewer.data.infrastructure.mapper.toFile
import com.sorrowblue.comicviewer.data.model.FileModel
import com.sorrowblue.comicviewer.data.model.ReadLaterFileModel
import com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.repository.ReadLaterRepository
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

fun ReadLaterFile.toModel() = ReadLaterFileModel(BookshelfModelId.from(bookshelfId), path)

fun ReadLaterFileModel.toReadLater() = ReadLaterFile(bookshelfModelId.toBookshelfId(), path)
