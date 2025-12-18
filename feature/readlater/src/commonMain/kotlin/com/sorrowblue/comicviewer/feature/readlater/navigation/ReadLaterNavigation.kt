package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenContext
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenRoot
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.file.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.folder.navigation.folderFileInfoNavEntry
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.toPair
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.takahirom.rin.rememberRetained
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@ContributesTo(AppScope::class)
interface ReadLaterNavigation {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(ReadLaterNavKey.Main.serializer()),
            toPair(ReadLaterNavKey.FileInfo.serializer()),
            toPair(ReadLaterNavKey.Folder.serializer()),
            toPair(ReadLaterNavKey.FolderFileInfo.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideNavigationKey(): NavigationKey = ReadLaterNavKey.Main

    @Provides
    @IntoSet
    private fun provideReadLaterEntry(
        factory: ReadLaterScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<ReadLaterNavKey.Main>(
            metadata = SupportingPaneSceneStrategy.mainPane("ReadLater") +
                NavDisplay.transitionMaterialFadeThrough(),
        ) {
            with(rememberRetained { factory.createReadLaterScreenContext() }) {
                ReadLaterScreenRoot(
                    onSettingsClick = { navigator.navigate(SettingsNavKey) },
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
                                    ReadLaterNavKey.Folder(
                                        file.bookshelfId,
                                        file.path,
                                    ),
                                )
                            }
                        }
                    },
                    onFileInfoClick = {
                        navigator.navigate(ReadLaterNavKey.FileInfo(it.key()))
                    },
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideReadLaterFileInfoEntry(
        factory: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            fileInfoEntry<ReadLaterNavKey.FileInfo>(
                sceneKey = "ReadLater",
                onBackClick = navigator::goBack,
                onCollectionClick = {
                    navigator.navigate(BasicCollectionAddNavKey(it.bookshelfId, it.path))
                },
                onOpenFolderClick = {
                    navigator.navigate(ReadLaterNavKey.Folder(it.bookshelfId, it.parent, it.path))
                },
            )
        }
    }

    @Provides
    @IntoSet
    private fun provideReadLaterFolderFileInfoEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factoryFolder) {
            with(factoryFileInfo) {
                folderFileInfoNavEntry<ReadLaterNavKey.Folder, ReadLaterNavKey.FolderFileInfo>(
                    sceneKeyPrefix = "ReadLater",
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
                                if (navigator.backStack.lastOrNull() is ReadLaterNavKey.FolderFileInfo) {
                                    navigator.goBack()
                                }
                                navigator.navigate(
                                    ReadLaterNavKey.Folder(file.bookshelfId, file.path),
                                )
                            }
                        }
                    },
                    onFileInfoClick = {
                        navigator.navigate(ReadLaterNavKey.FolderFileInfo(it.key()))
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
