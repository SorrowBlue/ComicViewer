package com.sorrowblue.comicviewer.data.remote.client.device

import android.content.Context
import android.content.Intent
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.sorrowblue.comicviewer.data.common.DeviceStorageModel
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.data.common.extension
import com.sorrowblue.comicviewer.data.remote.client.FileClient
import com.sorrowblue.comicviewer.data.remote.client.SeekableInputStream
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream

internal class DeviceFileClient @AssistedInject constructor(
    @Assisted override val serverModel: DeviceStorageModel,
    @ApplicationContext private val context: Context
) : FileClient {

    @AssistedFactory
    interface Factory : FileClient.Factory<DeviceStorageModel> {
        override fun create(serverModel: DeviceStorageModel): DeviceFileClient
    }

    private val contentResolver = context.contentResolver

    override suspend fun inputStream(fileModel: FileModel): InputStream {
        return ParcelFileDescriptor.AutoCloseInputStream(
            contentResolver.openFileDescriptor(fileModel.uri, "r")
        )
    }

    override suspend fun exists(fileModel: FileModel): Boolean {
        return fileModel.documentFile.exists()
    }

    override suspend fun exists(path: String): Boolean {
        return documentFile(path).exists().also {
            if (it) {
                contentResolver.takePersistableUriPermission(
                    path.toUri(),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
    }

    override suspend fun current(path: String): FileModel {
        return documentFile(path).toFileModel()
    }

    override suspend fun current(fileModel: FileModel): FileModel {
        return fileModel.documentFile.toFileModel()
    }

    override suspend fun listFiles(
        fileModel: FileModel,
        resolveImageFolder: Boolean
    ): List<FileModel> {
        return fileModel.documentFile.listFiles().map { it.toFileModel(resolveImageFolder) }
    }

    override suspend fun seekableInputStream(fileModel: FileModel): SeekableInputStream {
        return DeviceSeekableInputStream(context, fileModel.uri)
    }

    private fun DocumentFile.toFileModel(resolveImageFolder: Boolean = false): FileModel {
        return if (resolveImageFolder && listFiles().any { it.name.orEmpty().extension in SUPPORTED_IMAGE }) {
            FileModel.ImageFolder(
                path = uri.toString(),
                serverModelId = serverModel.id,
                name = name?.removeSuffix("/").orEmpty(),
                parent = parentFile?.uri?.toString().orEmpty(),
                size = length(),
                lastModifier = lastModified(),
                sortIndex = 0,
                cacheKey = "",
                totalPageCount = 0,
                lastReadPage = 0,
                lastRead = 0
            )
        } else if (isFile) {
            FileModel.File(
                path = uri.toString(),
                serverModelId = serverModel.id,
                name = name?.removeSuffix("/").orEmpty(),
                parent = parentFile?.uri?.toString().orEmpty(),
                size = length(),
                lastModifier = lastModified(),
                sortIndex = 0,
                cacheKey = "",
                totalPageCount = 0,
                lastReadPage = 0,
                lastRead = 0
            )
        } else {
            FileModel.Folder(
                path = uri.toString(),
                serverModelId = serverModel.id,
                name = name?.removeSuffix("/").orEmpty(),
                parent = parentFile?.uri?.toString().orEmpty(),
                size = length(),
                lastModifier = lastModified(),
                sortIndex = 0
            )
        }
    }

    private val FileModel.uri get() = path.toUri()

    private fun documentFile(path: String) = DocumentFile.fromTreeUri(context, path.toUri())!!

    private val FileModel.documentFile: DocumentFile
        get() = DocumentFile.fromTreeUri(
            context,
            uri
        )!!
}
