package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.collection.CollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.CollectionScreenRoot
import com.sorrowblue.comicviewer.feature.collection.add.BasicCollectionAddScreenContext
import com.sorrowblue.comicviewer.feature.collection.add.BasicCollectionAddScreenRoot
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollectionScreenRoot
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenRoot
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenRoot
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenRoot
import com.sorrowblue.comicviewer.feature.collection.list.CollectionListScreenContext
import com.sorrowblue.comicviewer.feature.collection.list.CollectionListScreenRoot
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.file.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.folder.navigation.folderFileInfoNavEntry
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
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
interface CollectionNavigation {
    @Provides
    @IntoSet
    private fun provideNavigationKey(): NavigationKey = CollectionNavKey.Main

    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(CollectionNavKey.Main.serializer()),
            toPair(CollectionNavKey.Detail.serializer()),
            toPair(CollectionNavKey.FileInfo.serializer()),
            toPair(CollectionNavKey.Folder.serializer()),
            toPair(CollectionNavKey.FolderFileInfo.serializer()),
            toPair(CollectionNavKey.BasicCreate.serializer()),
            toPair(CollectionNavKey.BasicEdit.serializer()),
            toPair(CollectionNavKey.SmartCreate.serializer()),
            toPair(CollectionNavKey.SmartEdit.serializer()),
            toPair(CollectionNavKey.Delete.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideCollectionListEntry(
        factory: CollectionListScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<CollectionNavKey.Main>(metadata = NavDisplay.transitionMaterialFadeThrough()) {
            with(rememberRetained { factory.createCollectionListScreenContext() }) {
                CollectionListScreenRoot(
                    onItemClick = { navigator.navigate(CollectionNavKey.Detail(it.id)) },
                    onEditClick = { },
                    onDeleteClick = { },
                    onSettingsClick = { navigator.navigate(SettingsNavKey) },
                    onCreateBasicCollectionClick = {
                        navigator.navigate(
                            CollectionNavKey.BasicCreate(),
                        )
                    },
                    onCreateSmartCollectionClick = {
                        navigator.navigate(
                            CollectionNavKey.SmartCreate(),
                        )
                    },
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideCollectionEntry(
        factory: CollectionScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<CollectionNavKey.Detail>(
            metadata = SupportingPaneSceneStrategy.mainPane("Collection") +
                NavDisplay.transitionMaterialSharedAxisZ(),
        ) { detail ->
            with(rememberRetained { factory.createCollectionScreenContext() }) {
                CollectionScreenRoot(
                    id = detail.id,
                    onBackClick = navigator::goBack,
                    onFileClick = { file ->
                        when (file) {
                            is Book -> {
                                navigator.navigate(
                                    BookNavKey(
                                        bookshelfId = file.bookshelfId,
                                        path = file.path,
                                        name = file.name,
                                        collectionId = detail.id,
                                    ),
                                )
                            }

                            is Folder -> {
                                navigator.navigate(
                                    CollectionNavKey.Folder(
                                        bookshelfId = file.bookshelfId,
                                        path = file.path,
                                    ),
                                )
                            }
                        }
                    },
                    onFileInfoClick = {
                        navigator.navigate(CollectionNavKey.FileInfo(it.key()))
                    },
                    onEditClick = { navigator.navigate(CollectionNavKey.BasicEdit(it)) },
                    onDeleteClick = { navigator.navigate(CollectionNavKey.Delete(it)) },
                    onSettingsClick = { navigator.navigate(SettingsNavKey) },
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideCollectionFileInfoEntry(
        factory: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            fileInfoEntry<CollectionNavKey.FileInfo>(
                sceneKey = "Collection",
                onBackClick = navigator::goBack,
                onCollectionClick = {
                    navigator.navigate(BasicCollectionAddNavKey(it.bookshelfId, it.path))
                },
                onOpenFolderClick = {
                    navigator.navigate(CollectionNavKey.Folder(it.bookshelfId, it.parent, it.path))
                },
            )
        }
    }

    @Provides
    @IntoSet
    private fun provideCollectionFolderInfoEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factoryFolder) {
            with(factoryFileInfo) {
                folderFileInfoNavEntry<CollectionNavKey.Folder, CollectionNavKey.FolderFileInfo>(
                    sceneKeyPrefix = "Collection",
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
                                if (navigator.backStack.lastOrNull() is CollectionNavKey.FolderFileInfo) {
                                    navigator.goBack()
                                }
                                navigator.navigate(
                                    CollectionNavKey.Folder(
                                        file.bookshelfId,
                                        file.path,
                                        null,
                                    ),
                                )
                            }
                        }
                    },
                    onFileInfoClick = {
                        navigator.navigate(CollectionNavKey.FolderFileInfo(it.key()))
                    },
                    onSettingsClick = { navigator.navigate(SettingsNavKey) },
                    onCollectionClick = {
                        navigator.navigate(BasicCollectionAddNavKey(it.bookshelfId, it.path))
                    },
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideBasicCollectionCreateEntry(
        factory: BasicCollectionCreateScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<CollectionNavKey.BasicCreate>(
            metadata = DialogSceneStrategy.dialog(
                DialogProperties(usePlatformDefaultWidth = false),
            ),
        ) {
            with(rememberRetained { factory.createBasicCollectionCreateScreenContext() }) {
                BasicCollectionCreateScreenRoot(
                    bookshelfId = it.bookshelfId,
                    path = it.path,
                    onBackClick = navigator::goBack,
                    onComplete = navigator::goBack,
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideBasicCollectionEditEntry(
        factory: BasicCollectionEditScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<CollectionNavKey.BasicEdit>(
            metadata = DialogSceneStrategy.dialog(
                DialogProperties(usePlatformDefaultWidth = false),
            ),
        ) {
            with(rememberRetained { factory.createBasicCollectionEditScreenContext() }) {
                BasicCollectionEditScreenRoot(
                    collectionId = it.collectionId,
                    onBackClick = navigator::goBack,
                    onComplete = navigator::goBack,
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideSmartCollectionCreateEntry(
        factory: SmartCollectionCreateScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            smartCollectionCreateEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideSmartCollectionEditEntry(
        factory: SmartCollectionEditScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<CollectionNavKey.SmartEdit>(
            metadata = DialogSceneStrategy.dialog(
                DialogProperties(usePlatformDefaultWidth = false),
            ),
        ) {
            with(rememberRetained { factory.createSmartCollectionEditScreenContext() }) {
                SmartCollectionEditScreenRoot(
                    collectionId = it.collectionId,
                    onCancelClick = navigator::goBack,
                    onComplete = navigator::goBack,
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideCollectionDeleteEntry(
        factory: DeleteCollectionScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<CollectionNavKey.Delete>(metadata = NavDisplay.transitionMaterialFadeThrough()) {
            with(rememberRetained { factory.createDeleteCollectionScreenContext() }) {
                DeleteCollectionScreenRoot(
                    id = it.id,
                    onBackClick = navigator::goBack,
                    onComplete = {
                        navigator.pop<CollectionNavKey.Main>(false)
                    },
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideBasicCollectionAddEntry(
        factory: BasicCollectionAddScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<BasicCollectionAddNavKey>(
            metadata = DialogSceneStrategy.dialog(
                DialogProperties(
                    usePlatformDefaultWidth = false,
                ),
            ),
        ) {
            with(rememberRetained { factory.createBasicCollectionAddScreenContext() }) {
                BasicCollectionAddScreenRoot(
                    bookshelfId = it.bookshelfId,
                    path = it.path,
                    onBackClick = navigator::goBack,
                    onCollectionCreateClick = { id, path ->
                        navigator.navigate(CollectionNavKey.BasicCreate(id, path))
                    },
                )
            }
        }
    }
}
