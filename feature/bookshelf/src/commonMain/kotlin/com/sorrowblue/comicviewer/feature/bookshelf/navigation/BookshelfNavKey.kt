package com.sorrowblue.comicviewer.feature.bookshelf.navigation

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditorType
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.folder.navigation.FolderNavKey
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import comicviewer.feature.bookshelf.generated.resources.Res
import comicviewer.feature.bookshelf.generated.resources.bookshelf_label_bookshelf
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
sealed interface BookshelfNavKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.bookshelf_label_bookshelf)

    override val icon get() = ComicIcons.LibraryBooks

    @Serializable
    data object Main : BookshelfNavKey

    @Serializable
    data object Selection : BookshelfNavKey

    @Serializable
    data class Edit(val type: BookshelfEditorType) : BookshelfNavKey

    @Serializable
    data class Info(val id: BookshelfId) : BookshelfNavKey

    @Serializable
    data class Delete(val id: BookshelfId) : BookshelfNavKey

    @Serializable
    data class Notification(val scanType: ScanType) : BookshelfNavKey

    @Serializable
    data class Folder(
        override val bookshelfId: BookshelfId,
        override val path: String,
        override val restorePath: String? = null,
        override val onRestoreComplete: (() -> Unit)? = null,
    ) : BookshelfNavKey,
        FolderNavKey {
        override val showSearch = true
    }

    @Serializable
    data class FolderFileInfo(override val fileKey: File.Key) :
        BookshelfNavKey,
        FileInfoNavKey {
        override val isOpenFolderEnabled: Boolean = false
    }
}
