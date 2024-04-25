package com.sorrowblue.comicviewer.data.service

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.service.FileScanService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class FileScanServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : FileScanService {

    override suspend fun enqueue(
        bookshelfId: BookshelfId,
        resolveImageFolder: Boolean,
        supportExtensions: List<String>,
    ): String {
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
            .setInputData(
                FileScanRequest(
                    bookshelfId,
                    resolveImageFolder,
                    supportExtensions
                ).toWorkData()
            )
            .build()
        val id = myWorkRequest.id
        WorkManager.getInstance(context)
//            .beginUniqueWork("scan", ExistingWorkPolicy.APPEND, myWorkRequest)
            .enqueue(myWorkRequest)
        return id.toString()
    }
}
