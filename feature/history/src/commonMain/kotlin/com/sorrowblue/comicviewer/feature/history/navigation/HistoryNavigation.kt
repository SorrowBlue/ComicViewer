package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.feature.history.ClearAllHistoryScreenResultKey
import com.sorrowblue.comicviewer.feature.history.ClearAllHistoryScreenRoot
import com.sorrowblue.comicviewer.feature.history.HistoryScreenContext
import com.sorrowblue.comicviewer.feature.history.HistoryScreenRoot
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.file.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.folder.navigation.FolderNavKey
import com.sorrowblue.comicviewer.folder.navigation.folderFileInfoNavEntry
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.toPair
import com.sorrowblue.comicviewer.framework.ui.navigation3.mainPane
import comicviewer.feature.history.generated.resources.Res
import comicviewer.feature.history.generated.resources.history_title
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.irgaly.navigation3.resultstate.NavigationResultMetadata
import io.github.irgaly.navigation3.resultstate.resultConsumer
import io.github.takahirom.rin.rememberRetained
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
sealed interface HistoryNavKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.history_title)

    override val icon get() = ComicIcons.History

    @Serializable
    data object Main : HistoryNavKey

    @Serializable
    data class FileInfo(override val fileKey: File.Key) :
        HistoryNavKey,
        FileInfoNavKey {
        override val isOpenFolderEnabled: Boolean = true
    }

    @Serializable
    data object ClearAll : HistoryNavKey

    @Serializable
    data class Folder(
        override val bookshelfId: BookshelfId,
        override val path: String,
        override val restorePath: String? = null,
    ) : HistoryNavKey,
        FolderNavKey

    @Serializable
    data class FolderFileInfo(override val fileKey: File.Key) :
        HistoryNavKey,
        FileInfoNavKey {
        override val isOpenFolderEnabled: Boolean = false
    }
}

@ContributesTo(AppScope::class)
interface HistoryNavigation {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(HistoryNavKey.Main.serializer()),
            toPair(HistoryNavKey.FileInfo.serializer()),
            toPair(HistoryNavKey.FolderFileInfo.serializer()),
            toPair(HistoryNavKey.ClearAll.serializer()),
            toPair(HistoryNavKey.Folder.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideNavigationKey(): NavigationKey = HistoryNavKey.Main

    @Provides
    @IntoSet
    private fun provideHistoryEntry(
        factory: HistoryScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<HistoryNavKey.Main>(
            metadata = SupportingPaneSceneStrategy.mainPane<HistoryNavKey.FileInfo>("History") +
                NavigationResultMetadata.resultConsumer(ClearAllHistoryScreenResultKey) +
                NavDisplay.transitionMaterialFadeThrough(),
        ) {
            with(rememberRetained { factory.createHistoryScreenContext() }) {
                HistoryScreenRoot(
                    onDeleteAllClick = {
                        navigator.navigate(HistoryNavKey.ClearAll)
                    },
                    onSettingsClick = { navigator.navigate(SettingsNavKey) },
                    onBookClick = { book ->
                        navigator.navigate(
                            BookNavKey(
                                bookshelfId = book.bookshelfId,
                                path = book.path,
                                name = book.name,
                            ),
                        )
                    },
                    onBookInfoClick = {
                        navigator.navigate(HistoryNavKey.FileInfo(it.key()))
                    },
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideHistoryClearAllEntry(): EntryProviderScope<NavKey>.(Navigator) -> Unit =
        { navigator ->
            entry<HistoryNavKey.ClearAll>(metadata = DialogSceneStrategy.dialog()) {
                ClearAllHistoryScreenRoot(onClose = navigator::goBack)
            }
        }

    @Provides
    @IntoSet
    private fun provideHistoryFileInfoEntry(
        factory: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            fileInfoEntry<HistoryNavKey.FileInfo>(
                sceneKey = "History",
                onBackClick = navigator::goBack,
                onCollectionClick = {
                    navigator.navigate(BasicCollectionAddNavKey(it.bookshelfId, it.path))
                },
                onOpenFolderClick = {
                    navigator.navigate(HistoryNavKey.Folder(it.bookshelfId, it.parent, it.path))
                },
            )
        }
    }

    @Provides
    @IntoSet
    private fun provideHistoryFolderFileInfoEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factoryFolder) {
            with(factoryFileInfo) {
                folderFileInfoNavEntry<HistoryNavKey.Folder, HistoryNavKey.FolderFileInfo>(
                    sceneKeyPrefix = "History",
                    onBackClick = navigator::goBack,
                    onFileClick = { file ->
                        when (file) {
                            is Book -> {
                                navigator.navigate(
                                    BookNavKey(
                                        bookshelfId = file.bookshelfId,
                                        path = file.path,
                                        name = file.name,
                                    ),
                                )
                            }

                            is Folder -> {
                                if (navigator.backStack.lastOrNull() is HistoryNavKey.FolderFileInfo) {
                                    navigator.goBack()
                                }
                                navigator.navigate(
                                    HistoryNavKey.Folder(file.bookshelfId, file.path),
                                )
                            }
                        }
                    },
                    onFileInfoClick = {
                        navigator.navigate(HistoryNavKey.FolderFileInfo(it.key()))
                    },
                    onSettingsClick = { navigator.navigate(SettingsNavKey) },
                    onCollectionClick = {
                        navigator.navigate(BasicCollectionAddNavKey(it.bookshelfId, it.path))
                    },
                )
            }
        }
    }
}
