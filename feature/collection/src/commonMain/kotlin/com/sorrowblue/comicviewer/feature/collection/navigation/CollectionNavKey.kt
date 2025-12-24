package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File
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
data object CollectionListNavKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.collection_title)

    override val icon get() = ComicIcons.CollectionsBookmark

    override val order get() = 2
}

@Serializable
internal data class CollectionNavKey(val id: CollectionId) : ScreenKey

@Serializable
internal data class CollectionFileInfoNavKey(override val fileKey: File.Key) : FileInfoNavKey {
    override val isOpenFolderEnabled: Boolean = true
}

@Serializable
internal data class CollectionFolderNavKey(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String? = null,
) : FolderNavKey

@Serializable
internal data class CollectionFolderFileInfoNavKey(override val fileKey: File.Key) :
    FileInfoNavKey {
    override val isOpenFolderEnabled: Boolean = false
}

@Serializable
internal data class CollectionDeleteNavKey(val id: CollectionId) : ScreenKey
