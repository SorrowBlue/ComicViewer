package com.sorrowblue.comicviewer.domain.model.bookshelf

import com.sorrowblue.comicviewer.domain.model.InternalDataApi

data class InternalStorage private constructor(
    override val id: BookshelfId,
    override val displayName: String,
    override val fileCount: Int,
    override val isDeleted: Boolean,
) : Bookshelf {
    override val type = BookshelfType.DEVICE

    fun copy(displayName: String = this.displayName) = copy(id = id, displayName = displayName)

    companion object {
        operator fun invoke(displayName: String) = InternalStorage(
            id = BookshelfId(),
            displayName = displayName,
            fileCount = 0,
            isDeleted = false,
        )

        @InternalDataApi
        operator fun invoke(
            id: BookshelfId,
            displayName: String,
            fileCount: Int,
            isDeleted: Boolean,
        ) = InternalStorage(
            id = id,
            displayName = displayName,
            fileCount = fileCount,
            isDeleted = isDeleted,
        )
    }
}
