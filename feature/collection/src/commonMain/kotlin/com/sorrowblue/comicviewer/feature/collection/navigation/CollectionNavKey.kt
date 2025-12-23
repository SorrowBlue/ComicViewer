package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.folder.navigation.FolderNavKey
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
sealed interface CollectionNavKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.collection_title)

    override val icon get() = ComicIcons.CollectionsBookmark

    override val order get() = 2

    @Serializable
    data object Main : CollectionNavKey

    @Serializable
    data class Detail(val id: CollectionId) : CollectionNavKey

    @Serializable
    data class FileInfo(override val fileKey: File.Key) :
        CollectionNavKey,
        FileInfoNavKey {
        override val isOpenFolderEnabled: Boolean = true
    }

    @Serializable
    data class Folder(
        override val bookshelfId: BookshelfId,
        override val path: String,
        override val restorePath: String? = null,
    ) : CollectionNavKey,
        FolderNavKey

    @Serializable
    data class FolderFileInfo(override val fileKey: File.Key) :
        CollectionNavKey,
        FileInfoNavKey {
        override val isOpenFolderEnabled: Boolean = false
    }

    @Serializable
    data class Delete(val id: CollectionId) : CollectionNavKey
}
