package com.sorrowblue.comicviewer.bookshelf

import androidx.work.Data
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.domain.model.ExperimentalIdValue
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

internal data class FileScanRequest(val bookshelfId: BookshelfId) {
    fun toWorkData() = workDataOf(ID to bookshelfId.value)

    companion object {

        const val ID = "id"

        fun fromWorkData(data: Data): FileScanRequest? {
            val id = data.getInt(ID, 0)
            if (id < 0) return null
            @OptIn(ExperimentalIdValue::class)
            return FileScanRequest(BookshelfId(id))
        }
    }
}
