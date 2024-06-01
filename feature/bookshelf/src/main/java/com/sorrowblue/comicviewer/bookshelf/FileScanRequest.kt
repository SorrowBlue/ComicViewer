package com.sorrowblue.comicviewer.bookshelf

import androidx.work.Data
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

internal class FileScanRequest(val bookshelfId: BookshelfId, val withThumbnails: Boolean) {
    fun toWorkData() = workDataOf(ID to bookshelfId.value, WITH_THUMBNAILS to withThumbnails)

    companion object {

        const val ID = "id"
        const val WITH_THUMBNAILS = "withThumbnails"

        fun fromWorkData(data: Data): FileScanRequest? {
            val id = data.getInt(ID, 0)
            if (id < 0) return null
            val withThumbnails = data.getBoolean(WITH_THUMBNAILS, false)
            return FileScanRequest(BookshelfId(id), withThumbnails)
        }
    }
}
