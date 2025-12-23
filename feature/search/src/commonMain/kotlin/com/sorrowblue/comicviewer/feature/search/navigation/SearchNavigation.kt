package com.sorrowblue.comicviewer.feature.search.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.feature.collection.nav.SmartCollectionCreateNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.file.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.folder.navigation.FolderNavKey
import com.sorrowblue.comicviewer.folder.navigation.folderFileInfoNavEntry
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.toPair
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.takahirom.rin.rememberRetained
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@ContributesTo(AppScope::class)
interface SearchNavigation {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(SearchNavKey.Main.serializer()),
            toPair(SearchNavKey.FileInfo.serializer()),
            toPair(SearchNavKey.FolderFileInfo.serializer()),
            toPair(SearchNavKey.Folder.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideSearchEntry(
        factory: com.sorrowblue.comicviewer.feature.search.SearchScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<SearchNavKey.Main>(
            metadata = SupportingPaneSceneStrategy.mainPane("Search") +
                NavDisplay.transitionMaterialSharedAxisX(),
        ) {
            with(rememberRetained { factory.createSearchScreenContext() }) {
                _root_ide_package_.com.sorrowblue.comicviewer.feature.search.SearchScreenRoot(
                    bookshelfId = it.bookshelfId,
                    path = it.path,
                    onBackClick = navigator::goBack,
                    onSettingsClick = { navigator.navigate(SettingsNavKey) },
                    onSmartCollectionClick = { id, condition ->
                        navigator.navigate(SmartCollectionCreateNavKey(id, condition))
                    },
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
                                navigator.navigate(
                                    SearchNavKey.Folder(file.bookshelfId, file.path),
                                )
                            }
                        }
                    },
                    onFileInfoClick = {
                        navigator.navigate(
                            SearchNavKey.Folder(it.bookshelfId, it.path),
                        )
                    },
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideHistoryFileInfoEntry(
        factory: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            fileInfoEntry<SearchNavKey.FileInfo>(
                "Search",
                onBackClick = navigator::goBack,
                onCollectionClick = {
                    navigator.navigate(BasicCollectionAddNavKey(it.bookshelfId, it.path))
                },
                onOpenFolderClick = {
                    navigator.navigate(
                        SearchNavKey.Folder(it.bookshelfId, it.parent, it.path),
                    )
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
                folderFileInfoNavEntry<SearchNavKey.Folder, SearchNavKey.FolderFileInfo>(
                    sceneKeyPrefix = "Search",
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
                                if (navigator.backStack.lastOrNull() is SearchNavKey.FolderFileInfo) {
                                    navigator.goBack()
                                }
                                navigator.navigate(
                                    SearchNavKey.Folder(file.bookshelfId, file.path),
                                )
                            }
                        }
                    },
                    onFileInfoClick = {
                        navigator.navigate(SearchNavKey.FolderFileInfo(it.key()))
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

@Serializable
sealed interface SearchNavKey : ScreenKey {
    @Serializable
    data class Main(val bookshelfId: BookshelfId, val path: PathString) : SearchNavKey

    @Serializable
    data class FileInfo(override val fileKey: File.Key) :
        SearchNavKey,
        FileInfoNavKey {
        override val isOpenFolderEnabled: Boolean = true
    }

    @Serializable
    data class Folder(
        override val bookshelfId: BookshelfId,
        override val path: String,
        override val restorePath: String? = null,
    ) : SearchNavKey,
        FolderNavKey

    @Serializable
    data class FolderFileInfo(override val fileKey: File.Key) :
        SearchNavKey,
        FileInfoNavKey {
        override val isOpenFolderEnabled: Boolean = false
    }
}
