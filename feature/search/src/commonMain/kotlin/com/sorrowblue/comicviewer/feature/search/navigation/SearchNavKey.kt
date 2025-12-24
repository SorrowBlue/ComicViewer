package com.sorrowblue.comicviewer.feature.search.navigation

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.folder.navigation.FolderNavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import kotlinx.serialization.Serializable

@Serializable
data class SearchNavKey(val bookshelfId: BookshelfId, val path: PathString) : ScreenKey

@Serializable
internal data class SearchFileInfoNavKey(override val fileKey: File.Key) : FileInfoNavKey {
    override val isOpenFolderEnabled: Boolean = true
}

@Serializable
internal data class SearchFolderNavKey(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String? = null,
) : FolderNavKey

@Serializable
internal data class SearchFolderFileInfoNavKey(override val fileKey: File.Key) :
    FileInfoNavKey {
    override val isOpenFolderEnabled: Boolean = false
}
