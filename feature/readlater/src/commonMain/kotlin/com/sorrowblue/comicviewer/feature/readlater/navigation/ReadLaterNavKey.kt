package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.folder.navigation.FolderNavKey
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import comicviewer.feature.readlater.generated.resources.Res
import comicviewer.feature.readlater.generated.resources.readlater_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
internal sealed interface ReadLaterNavKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.readlater_title)
    override val icon get() = ComicIcons.WatchLater

    @Serializable
    data object Main : ReadLaterNavKey

    @Serializable
    data class FileInfo(override val fileKey: File.Key) :
        ReadLaterNavKey,
        FileInfoNavKey {
        override val isOpenFolderEnabled: Boolean = true
    }

    @Serializable
    data class Folder(
        override val bookshelfId: BookshelfId,
        override val path: String,
        override val restorePath: String? = null,
    ) : ReadLaterNavKey,
        FolderNavKey

    @Serializable
    data class FolderFileInfo(override val fileKey: File.Key) :
        ReadLaterNavKey,
        FileInfoNavKey {
        override val isOpenFolderEnabled: Boolean = false
    }
}
