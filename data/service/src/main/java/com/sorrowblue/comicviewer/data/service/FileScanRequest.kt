package com.sorrowblue.comicviewer.data.service

import androidx.work.Data
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.model.model.ScanModel

internal class FileScanRequest(
    val bookshelfModelId: BookshelfModelId,
    val path: String,
    internal val scanModel: ScanModel,
    val resolveImageFolder: Boolean,
    val supportExtensions: List<String>
) {
    fun toWorkData() = workDataOf(
        SERVER_MODEL_ID to bookshelfModelId.value,
        PATH to path,
        SCAN_MODEL to scanModel.name,
        RESOLVE_IMAGE_FOLDER to resolveImageFolder,
        SUPPORT_TYPE_EXTENSIONS to supportExtensions.toTypedArray()
    )

    companion object {

        const val SERVER_MODEL_ID = "serverModelId"
        const val PATH = "serverModelPath"
        const val RESOLVE_IMAGE_FOLDER = "resolveImageFolder"
        const val SCAN_MODEL = "scanModel"
        const val SUPPORT_TYPE_EXTENSIONS = "supportExtensions"

        fun fromWorkData(data: Data): FileScanRequest? {
            val id = data.getInt(SERVER_MODEL_ID, 0)
            val path = data.getString(PATH) ?: return null
            val scanModel =
                data.getString(SCAN_MODEL)?.let(ScanModel::valueOf) ?: return null
            val resolveImageFolder = data.getBoolean(RESOLVE_IMAGE_FOLDER, false)
            val supportExtensions =
                data.getStringArray(SUPPORT_TYPE_EXTENSIONS)?.asList() ?: return null
            if (id < 0) return null
            return FileScanRequest(
                BookshelfModelId(id),
                path,
                scanModel,
                resolveImageFolder,
                supportExtensions
            )
        }
    }
}
