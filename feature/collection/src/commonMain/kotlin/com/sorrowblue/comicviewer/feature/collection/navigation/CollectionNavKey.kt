package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.folder.navigation.FolderNavKey
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
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
    ) : CollectionNavKey, FolderNavKey

    @Serializable
    data class FolderFileInfo(override val fileKey: File.Key) :
        CollectionNavKey,
        FileInfoNavKey {
        override val isOpenFolderEnabled: Boolean = false
    }


    @Serializable
    data class BasicCreate(
        val bookshelfId: BookshelfId = BookshelfId.Companion(),
        val path: PathString = "",
    ) : CollectionNavKey

    @Serializable
    data class BasicEdit(val collectionId: CollectionId) :
        CollectionNavKey

    @Serializable
    data class SmartCreate(
        val bookshelfId: BookshelfId? = null,
        val searchCondition: SearchCondition = SearchCondition(),
    ) : CollectionNavKey

    @Serializable
    data class SmartEdit(val collectionId: CollectionId) : CollectionNavKey

    @Serializable
    data class Delete(val id: CollectionId) : CollectionNavKey
}
