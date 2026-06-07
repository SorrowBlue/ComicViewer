package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.work.WorkManager
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.FileScanWorker
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.ThumbnailScanWorker
import com.sorrowblue.comicviewer.framework.common.annotation.VisibleForAssistedInject
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(VisibleForAssistedInject::class)
@AssistedInject
internal class BookshelfInfoContentViewModel(
    @Assisted private val bookshelfFolder: BookshelfFolder,
    private val workManager: WorkManager,
    private val pagingBookshelfBookUseCase: PagingBookshelfBookUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingBookshelfBookUseCase(
        PagingBookshelfBookUseCase.Request(
            bookshelfFolder.bookshelf.id,
            PagingConfig(PageSize),
        ),
    ).cachedIn(viewModelScope)

    val isScanningFile =
        FileScanWorker.getWorkInfosFlow(workManager, bookshelfFolder.bookshelf.id)
            .map { workInfos -> workInfos.any { !it.state.isFinished } }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                false,
            )
    val isScanningThumbnail =
        ThumbnailScanWorker.getWorkInfosFlow(workManager, bookshelfFolder.bookshelf.id)
            .map { workInfos -> workInfos.any { !it.state.isFinished } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun scanFile() {
        FileScanWorker.enqueueUniqueWork(workManager, bookshelfFolder.bookshelf.id)
    }

    fun scanThumbnail() {
        ThumbnailScanWorker.enqueueUniqueWork(workManager, bookshelfFolder.bookshelf.id)
    }

    @AssistedFactory
    interface Factory : ManualViewModelAssistedFactory {
        fun create(bookshelfFolder: BookshelfFolder): BookshelfInfoContentViewModel
    }
}

private const val PageSize = 4

@VisibleForAssistedInject
@ContributesTo(AppScope::class)
interface BookshelfContentViewModelModule {

    @IntoMap
    @ManualViewModelAssistedFactoryKey
    @Binds
    private val BookshelfInfoContentViewModel.Factory.bind get(): ManualViewModelAssistedFactory = this
}
