package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenContext
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenRoot
import com.sorrowblue.comicviewer.folder.navigation.FileInfoKey
import com.sorrowblue.comicviewer.folder.navigation.FolderKey
import com.sorrowblue.comicviewer.folder.navigation.SortTypeSelectKey
import com.sorrowblue.comicviewer.folder.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.folder.navigation.folderEntryGroup
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import comicviewer.feature.readlater.generated.resources.Res
import comicviewer.feature.readlater.generated.resources.readlater_title
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.stringResource

val ReadLaterKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(ReadLaterKey.List::class, ReadLaterKey.List.serializer())
        subclass(ReadLaterKey.FileInfo::class, ReadLaterKey.FileInfo.serializer())
        subclass(ReadLaterKey.FileInfo2::class, ReadLaterKey.FileInfo2.serializer())
        subclass(ReadLaterKey.Folder::class, ReadLaterKey.Folder.serializer())
    }
}

@Serializable
sealed interface ReadLaterKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.readlater_title)
    override val icon get() = ComicIcons.WatchLater

    @Serializable
    data object List : ReadLaterKey

    @Serializable
    data class FileInfo(override val fileKey: File.Key) :
        ReadLaterKey,
        FileInfoKey {
        override val isOpenFolderEnabled: Boolean = true
    }

    @Serializable
    data class FileInfo2(override val fileKey: File.Key) :
        ReadLaterKey,
        FileInfoKey {
        override val isOpenFolderEnabled: Boolean = true
    }

    @Serializable
    data class Folder(override val bookshelfId: BookshelfId, override val path: String) :
        ReadLaterKey,
        FolderKey {
        override val restorePath: String? = null
    }
}

context(context: PlatformContext)
fun EntryProviderScope<NavKey>.readLaterEntryGroup(
    navigator: Navigator,
    onSettingsClick: () -> Unit,
    onSearchClick: (BookshelfId, PathString) -> Unit,
    onBookClick: (Book) -> Unit,
    onCollectionClick: (File) -> Unit,
) {
    readLaterEntry(
        onSettingsClick = onSettingsClick,
        onFileClick = {
            when (it) {
                is Book -> onBookClick(it)

                is Folder -> {
                    navigator.navigate(
                        ReadLaterKey.Folder(it.bookshelfId, it.path),
                    )
                }
            }
        },
        onFileInfoClick = {
            navigator.navigate(ReadLaterKey.FileInfo(it.key()))
        },
    )
    readLaterFileInfoEntry(
        onBackClick = navigator::goBack,
        onCollectionClick = onCollectionClick,
        onOpenFolderClick = { /* Do noting */ },
    )
    folderEntryGroup<ReadLaterKey.Folder, ReadLaterKey.FileInfo2>(
        "ReadLater",
        onBackClick = navigator::goBack,
        onSearchClick = onSearchClick,
        onFileClick = { file ->
            when (file) {
                is Book -> onBookClick(file)

                is Folder -> navigator.navigate(
                    ReadLaterKey.Folder(file.bookshelfId, file.path),
                )
            }
        },
        onFileInfoClick = {
            navigator.navigate(ReadLaterKey.FileInfo2(it.key()))
        },
        onRestored = { /* Do noting */ },
        onSortClick = { sortType, folderScopeOnly ->
            navigator.navigate(SortTypeSelectKey(sortType, folderScopeOnly))
        },
        onSettingsClick = onSettingsClick,
        onCollectionClick = onCollectionClick,
        onOpenFolderClick = { /* Do noting */ },
    )
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.readLaterEntry(
    onSettingsClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
) {
    entryScreen<ReadLaterKey.List, ReadLaterScreenContext>(
        createContext = {
            context.require<ReadLaterScreenContext.Factory>().createReadLaterScreenContext()
        },
        metadata = SupportingPaneSceneStrategy.mainPane(
            "ReadLater",
        ) + NavDisplay.transitionMaterialFadeThrough(),
    ) {
        ReadLaterScreenRoot(
            onSettingsClick = onSettingsClick,
            onFileClick = onFileClick,
            onFileInfoClick = onFileInfoClick,
        )
    }
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.readLaterFileInfoEntry(
    onBackClick: () -> Unit,
    onCollectionClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
) {
    fileInfoEntry<ReadLaterKey.FileInfo>(
        "ReadLater",
        onBackClick = onBackClick,
        onCollectionClick = onCollectionClick,
        onOpenFolderClick = onOpenFolderClick,
    )
}
