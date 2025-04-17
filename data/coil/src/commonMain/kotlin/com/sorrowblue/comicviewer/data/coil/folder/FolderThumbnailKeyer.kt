package com.sorrowblue.comicviewer.data.coil.folder

import coil3.key.Keyer
import coil3.request.Options
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail

internal object FolderThumbnailKeyer : Keyer<FolderThumbnail> {
    override fun key(data: FolderThumbnail, options: Options) =
        "folder:${data.bookshelfId.value}:${data.path}"
}
