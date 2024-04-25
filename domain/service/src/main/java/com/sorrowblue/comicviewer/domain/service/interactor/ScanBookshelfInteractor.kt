package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.ScanBookshelfUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.first

internal class ScanBookshelfInteractor @Inject constructor(
//    private val fileScanService: FileScanService,
    private val datastoreDataSource: DatastoreDataSource,
) : ScanBookshelfUseCase() {
    override suspend fun run(request: Request): Result<String, Unit> {
        val folderSettings = datastoreDataSource.folderSettings.first()
        return TODO()
//        return fileScanService.enqueue(
//            bookshelfId,
//            folderSettings.resolveImageFolder,
//            folderSettings.supportExtension.map(SupportExtension::extension)
//        )
    }
}
