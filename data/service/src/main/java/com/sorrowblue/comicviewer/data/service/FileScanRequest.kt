package com.sorrowblue.comicviewer.data.service

import androidx.work.Data
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.domain.model.Scan
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

internal class FileScanRequest(
    val bookshelfId: BookshelfId,
    val path: String,
    internal val scan: Scan,
    val resolveImageFolder: Boolean,
    val supportExtensions: List<String>,
) {
    fun toWorkData() = workDataOf(
        ID to bookshelfId.value,
        PATH to path,
        SCAN to scan.name,
        RESOLVE_IMAGE_FOLDER to resolveImageFolder,
        SUPPORT_TYPE_EXTENSIONS to supportExtensions.toTypedArray()
    )

    companion object {

        const val ID = "id"
        const val PATH = "path"
        const val RESOLVE_IMAGE_FOLDER = "resolveImageFolder"
        const val SCAN = "scan"
        const val SUPPORT_TYPE_EXTENSIONS = "supportExtensions"

        fun fromWorkData(data: Data): FileScanRequest? {
            val id = data.getInt(ID, 0)
            val path = data.getString(PATH) ?: return null
            val scan = data.getString(SCAN)?.let(Scan::valueOf) ?: return null
            val resolveImageFolder = data.getBoolean(RESOLVE_IMAGE_FOLDER, false)
            val supportExtensions =
                data.getStringArray(SUPPORT_TYPE_EXTENSIONS)?.asList() ?: return null
            if (id < 0) return null
            return FileScanRequest(
                BookshelfId(id),
                path,
                scan,
                resolveImageFolder,
                supportExtensions
            )
        }
    }
}
