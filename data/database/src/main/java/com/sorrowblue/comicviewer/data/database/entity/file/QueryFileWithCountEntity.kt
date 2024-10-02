package com.sorrowblue.comicviewer.data.database.entity.file

import androidx.room.Embedded
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder

internal class QueryFileWithCountEntity(
    @Embedded val fileEntity: FileEntity,
    private val count: Int,
) {

    fun toModel(): File {
        return when (fileEntity.fileType) {
            FileEntity.Type.FILE -> BookFile(
                path = fileEntity.path,
                bookshelfId = fileEntity.bookshelfId,
                parent = fileEntity.parent,
                name = fileEntity.name,
                size = fileEntity.size,
                lastModifier = fileEntity.lastModified,
                isHidden = fileEntity.isHidden,
                sortIndex = fileEntity.sortIndex,
                cacheKey = fileEntity.info.cacheKey,
                totalPageCount = fileEntity.info.totalPageCount,
                lastPageRead = fileEntity.history.lastReadPage,
                lastReadTime = fileEntity.history.lastReading
            )

            FileEntity.Type.FOLDER -> Folder(
                path = fileEntity.path,
                bookshelfId = fileEntity.bookshelfId,
                name = fileEntity.name,
                parent = fileEntity.parent,
                size = fileEntity.size,
                lastModifier = fileEntity.lastModified,
                isHidden = fileEntity.isHidden,
                sortIndex = fileEntity.sortIndex,
                count = count
            )

            FileEntity.Type.IMAGE_FOLDER -> BookFolder(
                path = fileEntity.path,
                bookshelfId = fileEntity.bookshelfId,
                name = fileEntity.name,
                parent = fileEntity.parent,
                size = fileEntity.size,
                lastModifier = fileEntity.lastModified,
                isHidden = fileEntity.isHidden,
                sortIndex = fileEntity.sortIndex,
                cacheKey = fileEntity.info.cacheKey,
                totalPageCount = fileEntity.info.totalPageCount,
                lastPageRead = fileEntity.history.lastReadPage,
                lastReadTime = fileEntity.history.lastReading,
                count = count
            )
        }
    }
}
