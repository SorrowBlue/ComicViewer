package com.sorrowblue.comicviewer.data.coil

import coil.fetch.Fetcher
import com.sorrowblue.comicviewer.data.common.BookPageRequestData
import com.sorrowblue.comicviewer.data.common.FolderThumbnailOrderModel
import com.sorrowblue.comicviewer.data.common.ServerFileModel

interface RemoteFileThumbnailFetcherFactory : Fetcher.Factory<ServerFileModel> {
    interface Factory {
        fun create(folderThumbnailOrder: suspend () -> FolderThumbnailOrderModel): RemoteFileThumbnailFetcherFactory
    }
}

interface SmbFetcherFactory : Fetcher.Factory<BookPageRequestData>
