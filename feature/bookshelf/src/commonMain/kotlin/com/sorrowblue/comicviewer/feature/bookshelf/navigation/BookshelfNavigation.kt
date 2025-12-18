package com.sorrowblue.comicviewer.feature.bookshelf.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
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
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.folder.navigation.folderFileInfoNavEntry
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.mainPane
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.takahirom.rin.rememberRetained
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@ContributesTo(AppScope::class)
interface BookshelfNavigation {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> {
        return listOf(
            (BookshelfNavKey.Main::class as KClass<NavKey>) to (BookshelfNavKey.Main.serializer() as KSerializer<NavKey>),
            (BookshelfNavKey.Info::class as KClass<NavKey>) to (BookshelfNavKey.Info.serializer() as KSerializer<NavKey>),
            (BookshelfNavKey.Folder::class as KClass<NavKey>) to (BookshelfNavKey.Folder.serializer() as KSerializer<NavKey>),
            (BookshelfNavKey.FolderFileInfo::class as KClass<NavKey>) to (BookshelfNavKey.FolderFileInfo.serializer() as KSerializer<NavKey>),
            (BookshelfNavKey.Selection::class as KClass<NavKey>) to (BookshelfNavKey.Selection.serializer() as KSerializer<NavKey>),
            (BookshelfNavKey.Edit::class as KClass<NavKey>) to (BookshelfNavKey.Edit.serializer() as KSerializer<NavKey>),
            (BookshelfNavKey.Delete::class as KClass<NavKey>) to (BookshelfNavKey.Delete.serializer() as KSerializer<NavKey>),
            (BookshelfNavKey.Notification::class as KClass<NavKey>) to (BookshelfNavKey.Notification.serializer() as KSerializer<NavKey>),
        )
    }

    @Provides
    @IntoSet
    private fun provideNavigationKey(): NavigationKey = BookshelfNavKey.Main

    @Provides
    @IntoSet
    private fun provideBookshelfEntry(
        factory: BookshelfScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<BookshelfNavKey.Main>(
            metadata = SupportingPaneSceneStrategy.mainPane<BookshelfNavKey.Info>("Bookshelf")
                + NavDisplay.transitionMaterialFadeThrough(),
        ) {
            with(rememberRetained { factory.createBookshelfScreenContext() }) {
                BookshelfScreenRoot(
                    onSettingsClick = { navigator.navigate(SettingsNavKey) },
                    onFabClick = { navigator.navigate(BookshelfNavKey.Selection) },
                    onBookshelfClick = { id, path ->
                        navigator.navigate(BookshelfNavKey.Folder(id, path))
                    },
                    onBookshelfInfoClick = {
                        navigator.navigate(BookshelfNavKey.Info(it.bookshelf.id))
                    },
                )
            }
        }
    }


    @Provides
    @IntoSet
    private fun provideBookshelfInfoEntry(
        factory: BookshelfInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<BookshelfNavKey.Info>(
            metadata = SupportingPaneSceneStrategy.extraPane("Bookshelf")
                + NavDisplay.transitionMaterialSharedAxisX(),
        ) {
            with(rememberRetained { factory.createBookshelfInfoScreenContext() }) {
                BookshelfInfoScreenRoot(
                    bookshelfId = it.id,
                    onBackClick = navigator::goBack,
                    onRemoveClick = { navigator.navigate(BookshelfNavKey.Delete(it.id)) },
                    showNotificationPermissionRationale = {
                        navigator.navigate(
                            BookshelfNavKey.Notification(
                                it
                            )
                        )
                    },
                    onEditClick = { id, type ->
                        navigator.navigate(
                            BookshelfNavKey.Edit(BookshelfEditorType.Edit(id, type))
                        )
                    },
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideBookshelfNotificationEntry(): EntryProviderScope<NavKey>.(Navigator) -> Unit =
        { navigator ->
            entry<BookshelfNavKey.Notification>(metadata = DialogSceneStrategy.dialog()) {
                NotificationRequestScreenRoot(
                    scanType = it.scanType,
                    onBackClick = navigator::goBack,
                )
            }
        }

    @Provides
    @IntoSet
    private fun provideBookshelfSelectionEntry(): EntryProviderScope<NavKey>.(Navigator) -> Unit =
        { navigator ->
            entry<BookshelfNavKey.Selection>(
                metadata = DialogSceneStrategy.dialog(
                    dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
                )
            ) {
                BookshelfSelectionDialog(
                    onBackClick = navigator::goBack,
                    onTypeClick = {
                        navigator.navigate(
                            BookshelfNavKey.Edit(BookshelfEditorType.Register(it))
                        )
                    }
                )
            }
        }

    @Provides
    @IntoSet
    private fun provideBookshelfEditEntry(
        factory: BookshelfEditScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<BookshelfNavKey.Edit>(metadata = DialogSceneStrategy.dialog()) {
            with(rememberRetained { factory.createBookshelfEditScreenContext() }) {
                BookshelfEditorDialog(
                    type = it.type,
                    onBackClick = navigator::goBack,
                    discardConfirm = navigator::goBack,
                    onEditComplete = {
                        navigator.pop<BookshelfNavKey.Selection>(inclusive = true)
                    },
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideBookshelfDeleteEntry(
        factory: BookshelfDeleteScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<BookshelfNavKey.Delete>(metadata = DialogSceneStrategy.dialog()) {
            with(rememberRetained { factory.createBookshelfDeleteScreenContext() }) {
                BookshelfDeleteScreen(
                    bookshelfId = it.id,
                    onBackClick = navigator::goBack,
                    onComplete = { navigator.pop<BookshelfNavKey.Main>(false) },
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideBookshelfFolderFileInfoEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factoryFolder) {
            with(factoryFileInfo) {
                folderFileInfoNavEntry<BookshelfNavKey.Folder, BookshelfNavKey.FolderFileInfo>(
                    sceneKeyPrefix = "Bookshelf",
                    onBackClick = navigator::goBack,
                    onFileClick = { file ->
                        when (file) {
                            is Book -> {
                                navigator.navigate(
                                    BookNavKey(
                                        bookshelfId = file.bookshelfId,
                                        path = file.path,
                                        name = file.name
                                    )
                                )
                            }

                            is Folder -> {
                                if (navigator.backStack.lastOrNull() is BookshelfNavKey.FolderFileInfo) {
                                    navigator.goBack()
                                }
                                navigator.navigate(
                                    BookshelfNavKey.Folder(file.bookshelfId, file.path)
                                )
                            }
                        }
                    },
                    onFileInfoClick = {
                        navigator.navigate(BookshelfNavKey.FolderFileInfo(it.key()))
                    },
                    onSettingsClick = { navigator.navigate(SettingsNavKey) },
                    onCollectionClick = {
                        navigator.navigate(BasicCollectionAddNavKey(it.bookshelfId, it.path))
                    }
                )
            }
        }
    }
}
