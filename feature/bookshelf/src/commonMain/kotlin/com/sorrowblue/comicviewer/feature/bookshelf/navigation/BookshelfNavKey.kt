package com.sorrowblue.comicviewer.feature.bookshelf.navigation

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.folder.navigation.FolderNavKey
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import comicviewer.feature.bookshelf.generated.resources.Res
import comicviewer.feature.bookshelf.generated.resources.bookshelf_label_bookshelf
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data object BookshelfNavKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.bookshelf_label_bookshelf)

    override val icon get() = ComicIcons.LibraryBooks

    override val order get() = 1
}

@Serializable
data class BookshelfFolderNavKey(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String? = null,
    override val onRestoreComplete: (() -> Unit)? = null,
) : FolderNavKey {
    override val showSearch = true
}

@Serializable
internal data class BookshelfFolderFileInfoNavKey(override val fileKey: File.Key) : FileInfoNavKey {
    override val isOpenFolderEnabled: Boolean = false
}
