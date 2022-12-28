package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerModelId

internal const val SERVER_ID = "server_id"

@Entity(
    tableName = "file",
    primaryKeys = ["path", SERVER_ID],
    foreignKeys = [ForeignKey(
        entity = Server::class,
        parentColumns = ["id"],
        childColumns = [SERVER_ID],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = [SERVER_ID])]
)
data class File(
    val path: String,
    @ColumnInfo(name = SERVER_ID) val serverId: Int,
    val name: String,
    val parent: String,
    val size: Long,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "file_type") val fileType: Type,
    @ColumnInfo(name = "file_type_order") val fileTypeOrder: Int = fileType.order,
    @ColumnInfo(name = "sort_index") val sortIndex: Int,
    @Embedded val info: FileInfo = FileInfo(),
    @Embedded val history: FileHistory = FileHistory()
) {
    fun toFileModel(): FileModel {
        return when (fileType) {
            Type.FILE -> FileModel.File(
                path = path,
                serverModelId = ServerModelId(serverId),
                parent = parent,
                name = name,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex,
                cacheKey = info.cacheKey,
                totalPageCount = info.totalPageCount,
                lastReadPage = history.lastReadPage,
                lastRead = history.lastRead
            )
            Type.FOLDER -> FileModel.Folder(
                path = path,
                serverModelId = ServerModelId(serverId),
                name = name,
                parent = parent,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex
            )
            Type.IMAGE_FOLDER -> FileModel.ImageFolder(
                path = path,
                serverModelId = ServerModelId(serverId),
                name = name,
                parent = parent,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex,
                cacheKey = info.cacheKey,
                totalPageCount = info.totalPageCount,
                lastReadPage = history.lastReadPage,
                lastRead = history.lastRead,
            )
        }
    }

    enum class Type(val order: Int) {
        FILE(1),
        FOLDER(0),
        IMAGE_FOLDER(0)
    }
}

data class FileHistory(
    @ColumnInfo(name = "last_read_page") val lastReadPage: Int = 0,
    @ColumnInfo(name = "last_read") val lastRead: Long = 0,
)

data class FileInfo(
    @ColumnInfo(name = "cache_key") val cacheKey: String = "",
    @ColumnInfo(name = "total_page_count") val totalPageCount: Int = 0,
)

data class UpdateFileHistory(
    val path: String,
    @ColumnInfo(name = SERVER_ID) val serverId: Int,
    @ColumnInfo(name = "last_read_page") val lastReadPage: Int,
    @ColumnInfo(name = "last_read") val lastRead: Long
)

data class UpdateFileInfo(
    val path: String,
    @ColumnInfo(name = SERVER_ID) val serverId: Int,
    @ColumnInfo(name = "cache_key") val cacheKey: String = "",
    @ColumnInfo(name = "total_page_count") val totalPageCount: Int = 0,
)
