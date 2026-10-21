/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.storage.device.impl

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientKey
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactoryMap
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.common.IoDispatcher
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.CoroutineDispatcher

@AssistedInject
internal class ShareFileClient(
    @Assisted bookshelf: ShareContents,
    context: Context,
    fileReaderFactoryMap: FileReaderFactoryMap,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseAndroidFileClient<ShareContents>(bookshelf, context, fileReaderFactoryMap, dispatcher) {
    @ContributesIntoMap(AppScope::class)
    @FileClientKey(ShareContents::class)
    @AssistedFactory
    fun interface Factory : FileClient.Factory<ShareContents> {
        override fun create(bookshelf: ShareContents): ShareFileClient
    }

    override suspend fun current(path: String, resolveImageFolder: Boolean): File = runCommand {
        documentFile(path).toFileModel()
    }

    override suspend fun listFiles(file: File, resolveImageFolder: Boolean): List<File> =
        runCommand {
            documentFile(file.path).listFiles().map { it.toFileModel() }
        }

    override suspend fun fileSize(path: String): Long = 0

    private fun DocumentFile.toFileModel(): File = BookFile(
        path = uri.toString(),
        bookshelfId = ShareContents.id,
        name = name?.removeSuffix("/").orEmpty(),
        parent = parentFile?.uri?.toString().orEmpty(),
        size = length(),
        lastModifier = lastModified(),
        isHidden = false,
        sortIndex = 0,
        cacheKey = "",
        totalPageCount = 0,
        lastPageRead = 0,
        lastReadTime = 0,
    )

    override fun documentFile(path: String): DocumentFile =
        requireNotNull(DocumentFile.fromSingleUri(context, path.toUri()))
}
