package com.sorrowblue.comicviewer.bookshelf

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfFolderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@HiltViewModel
internal class BookshelfViewModel @Inject constructor(
    pagingBookshelfFolderUseCase: PagingBookshelfFolderUseCase,
    private val removeBookshelfUseCase: RemoveBookshelfUseCase,
    val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
) : ViewModel() {

    fun scan(bookshelfId: BookshelfId, withThumbnails: Boolean, context: Context) {
        val constraints = Constraints.Builder().apply {
            // 有効なネットワーク接続が必要
            setRequiredNetworkType(NetworkType.CONNECTED)
            // ユーザーのデバイスの保存容量が少なすぎる場合以外
            setRequiresStorageNotLow(true)
        }.build()

        val myWorkRequest = OneTimeWorkRequest.Builder(FileScanWorker::class.java)
            .setConstraints(constraints)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag("observable")
            .setInputData(FileScanRequest(bookshelfId, withThumbnails).toWorkData())
            .build()
        WorkManager.getInstance(context)
            .enqueue(myWorkRequest)
    }

    val pagingDataFlow: Flow<PagingData<BookshelfFolder>> =
        pagingBookshelfFolderUseCase.execute(PagingBookshelfFolderUseCase.Request(PagingConfig(20)))
            .cachedIn(viewModelScope)

    fun remove(bookshelf: Bookshelf) {
        viewModelScope.launch {
            removeBookshelfUseCase.execute(RemoveBookshelfUseCase.Request(bookshelf))
        }
    }
}
