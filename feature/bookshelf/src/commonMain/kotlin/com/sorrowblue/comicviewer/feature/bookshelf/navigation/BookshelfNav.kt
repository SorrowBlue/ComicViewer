package com.sorrowblue.comicviewer.feature.bookshelf.navigation

import androidx.compose.material3.adaptive.navigation3.kmp.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfScreenRoot
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditorDialog
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditorType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfSelectionDialog
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenRoot
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDeleteScreen
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDeleteScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequestScreenRoot
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.folder.navigation.FileInfoKey
import com.sorrowblue.comicviewer.folder.navigation.FolderKey
import com.sorrowblue.comicviewer.folder.navigation.SortTypeSelectKey
import com.sorrowblue.comicviewer.folder.navigation.folderEntryGroup
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.navigation.AppNavigationState
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import comicviewer.feature.bookshelf.generated.resources.Res
import comicviewer.feature.bookshelf.generated.resources.bookshelf_label_bookshelf
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.stringResource

val BookshelfKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(BookshelfKey.List::class, BookshelfKey.List.serializer())
        subclass(BookshelfKey.Selection::class, BookshelfKey.Selection.serializer())
        subclass(BookshelfKey.Edit::class, BookshelfKey.Edit.serializer())
        subclass(BookshelfKey.Info::class, BookshelfKey.Info.serializer())
        subclass(BookshelfKey.Delete::class, BookshelfKey.Delete.serializer())
        subclass(BookshelfKey.Notification::class, BookshelfKey.Notification.serializer())
        subclass(BookshelfKey.Folder::class, BookshelfKey.Folder.serializer())
        subclass(BookshelfKey.FileInfo::class, BookshelfKey.FileInfo.serializer())
    }
}

@Serializable
sealed interface BookshelfKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.bookshelf_label_bookshelf)

    override val icon get() = ComicIcons.LibraryBooks

    @Serializable
    data object List : BookshelfKey

    @Serializable
    data object Selection : BookshelfKey

    @Serializable
    data class Edit(val type: BookshelfEditorType) : BookshelfKey

    @Serializable
    data class Info(val id: BookshelfId) : BookshelfKey

    @Serializable
    data class Delete(val id: BookshelfId) : BookshelfKey

    @Serializable
    data class Notification(val scanType: ScanType) : BookshelfKey

    @Serializable
    data class Folder(
        override val bookshelfId: BookshelfId,
        override val path: String,
        override val restorePath: String?,
    ) : BookshelfKey,
        FolderKey

    @Serializable
    data class FileInfo(override val fileKey: File.Key) :
        BookshelfKey,
        FileInfoKey {
        override val isOpenFolderEnabled: Boolean = true
    }
}

context(graph: PlatformGraph, appNavigationState: AppNavigationState)
fun EntryProviderScope<NavKey>.bookshelfEntryGroup(
    onSettingsClick: () -> Unit,
    onSearchClick: (BookshelfId, PathString) -> Unit,
    onBookClick: (Book) -> Unit,
    onRestored: () -> Unit,
    onCollectionClick: (File) -> Unit,
) {
    bookshelfEntry(
        onSettingsClick = onSettingsClick,
        onFabClick = {
            appNavigationState.addToBackStack(BookshelfKey.Selection)
        },
        onBookshelfClick = { id, path ->
            appNavigationState.addToBackStack(
                BookshelfKey.Folder(
                    id,
                    path,
                    null,
                ),
            )
        },
        onBookshelfInfoClick = {
            appNavigationState.addToBackStack(BookshelfKey.Info(it.bookshelf.id))
        },
    )
    notificationEntry(
        onBackClick = appNavigationState::onBackPressed,
    )
    bookshelfSelectionEntry(
        onBackClick = appNavigationState::onBackPressed,
        onTypeClick = { type ->
            appNavigationState.addToBackStack(
                BookshelfKey.Edit(
                    BookshelfEditorType.Register(type),
                ),
            )
        },
    )
    bookshelfEditEntry(
        onBackClick = appNavigationState::onBackPressed,
        discardConfirm = appNavigationState::onBackPressed,
        onEditComplete = appNavigationState::onBackPressed,
    )
    bookshelfInfoEntry(
        onBackClick = appNavigationState::onBackPressed,
        onRemoveClick = { id ->
            appNavigationState.addToBackStack(BookshelfKey.Delete(id))
        },
        onEditClick = { id, type ->
            appNavigationState.addToBackStack(
                BookshelfKey.Edit(
                    BookshelfEditorType.Edit(id, type),
                ),
            )
        },
        showNotificationPermissionRationale = {
            appNavigationState.addToBackStack(BookshelfKey.Notification(it))
        },
    )
    bookshelfDeleteEntry(
        onBackClick = appNavigationState::onBackPressed,
        onComplete = appNavigationState::onBackPressed,
    )
    folderEntryGroup<BookshelfKey.Folder, BookshelfKey.FileInfo>(
        sceneKey = "BookshelfFolder",
        onBackClick = appNavigationState::onBackPressed,
        onSearchClick = onSearchClick,
        onFileClick = { file ->
            when (file) {
                is Book -> onBookClick(file)

                is Folder -> {
                    if (appNavigationState.currentBackStack.lastOrNull() is BookshelfKey.FileInfo) {
                        appNavigationState.currentBackStack.removeLastOrNull()
                    }
                    appNavigationState.addToBackStack(
                        BookshelfKey.Folder(
                            file.bookshelfId,
                            file.path,
                            null,
                        ),
                    )
                }
            }
        },
        onFileInfoClick = {
            appNavigationState.addToBackStack(BookshelfKey.FileInfo(it.key()))
        },
        onSortClick = { sortType, folderScopeOnly ->
            appNavigationState.addToBackStack(SortTypeSelectKey(sortType, folderScopeOnly))
        },
        onRestored = onRestored,
        onCollectionClick = onCollectionClick,
        onOpenFolderClick = { /* Do noting */ },
    )
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.bookshelfEntry(
    onSettingsClick: () -> Unit,
    onFabClick: () -> Unit,
    onBookshelfClick: (BookshelfId, PathString) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
) {
    entryScreen<BookshelfKey.List, BookshelfScreenContext>(
        createContext = {
            (graph as BookshelfScreenContext.Factory).createBookshelfScreenContext()
        },
        metadata = SupportingPaneSceneStrategy.mainPane("Bookshelf"),
    ) {
        BookshelfScreenRoot(
            onSettingsClick = onSettingsClick,
            onFabClick = onFabClick,
            onBookshelfClick = onBookshelfClick,
            onBookshelfInfoClick = onBookshelfInfoClick,
            onNotificationRequest = {},
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.bookshelfInfoEntry(
    onBackClick: () -> Unit,
    onRemoveClick: (BookshelfId) -> Unit,
    onEditClick: (BookshelfId, BookshelfType) -> Unit,
    showNotificationPermissionRationale: (ScanType) -> Unit,
) {
    entryScreen<BookshelfKey.Info, BookshelfInfoScreenContext>(
        createContext = {
            (graph as BookshelfInfoScreenContext.Factory)
                .createBookshelfInfoScreenContext()
        },
        metadata = SupportingPaneSceneStrategy.extraPane("Bookshelf"),
    ) {
        BookshelfInfoScreenRoot(
            bookshelfId = it.id,
            onBackClick = onBackClick,
            onRemoveClick = { onRemoveClick(it.id) },
            showNotificationPermissionRationale = showNotificationPermissionRationale,
            onEditClick = onEditClick,
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.notificationEntry(onBackClick: () -> Unit) {
    entry<BookshelfKey.Notification>(
        metadata = DialogSceneStrategy.dialog(),
    ) {
        NotificationRequestScreenRoot(
            scanType = it.scanType,
            onBackClick = onBackClick,
        )
    }
}

private fun EntryProviderScope<NavKey>.bookshelfSelectionEntry(
    onBackClick: () -> Unit,
    onTypeClick: (BookshelfType) -> Unit,
) {
    entry<BookshelfKey.Selection>(
        metadata = DialogSceneStrategy.dialog(
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = false,
            ),
        ),
    ) {
        BookshelfSelectionDialog(
            onBackClick = onBackClick,
            onTypeClick = onTypeClick,
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.bookshelfEditEntry(
    onBackClick: () -> Unit,
    discardConfirm: () -> Unit,
    onEditComplete: () -> Unit,
) {
    entryScreen<BookshelfKey.Edit, BookshelfEditScreenContext>(
        createContext = {
            (graph as BookshelfEditScreenContext.Factory)
                .createBookshelfEditScreenContext()
        },
        metadata = DialogSceneStrategy.dialog(),
    ) {
        BookshelfEditorDialog(
            type = it.type,
            onBackClick = onBackClick,
            discardConfirm = discardConfirm,
            onEditComplete = onEditComplete,
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.bookshelfDeleteEntry(
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    entryScreen<BookshelfKey.Delete, BookshelfDeleteScreenContext>(
        createContext = {
            (graph as BookshelfDeleteScreenContext.Factory).createBookshelfDeleteScreenContext()
        },
        metadata = DialogSceneStrategy.dialog(),
    ) {
        BookshelfDeleteScreen(
            it.id,
            onBackClick = onBackClick,
            onComplete = onComplete,
        )
    }
}
