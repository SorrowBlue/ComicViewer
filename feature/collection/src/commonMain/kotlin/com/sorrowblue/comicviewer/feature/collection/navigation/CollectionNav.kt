package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.feature.collection.CollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.CollectionScreenRoot
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollectionScreenRoot
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenRoot
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenRoot
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionCreateScreenRoot
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenRoot
import com.sorrowblue.comicviewer.feature.collection.list.CollectionListScreenContext
import com.sorrowblue.comicviewer.feature.collection.list.CollectionListScreenRoot
import com.sorrowblue.comicviewer.folder.navigation.FileInfoKey
import com.sorrowblue.comicviewer.folder.navigation.FolderKey
import com.sorrowblue.comicviewer.folder.navigation.SortTypeSelectKey
import com.sorrowblue.comicviewer.folder.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.folder.navigation.folderEntryGroup
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_title
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.stringResource

val CollectionKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(CollectionKey.List::class, CollectionKey.List.serializer())
        subclass(CollectionKey.Detail::class, CollectionKey.Detail.serializer())
        subclass(CollectionKey.CreateBasic::class, CollectionKey.CreateBasic.serializer())
        subclass(CollectionKey.EditBasic::class, CollectionKey.EditBasic.serializer())
        subclass(CollectionKey.CreateSmart::class, CollectionKey.CreateSmart.serializer())
        subclass(CollectionKey.EditSmart::class, CollectionKey.EditSmart.serializer())
        subclass(CollectionKey.Delete::class, CollectionKey.Delete.serializer())
        subclass(CollectionKey.Folder::class, CollectionKey.Folder.serializer())
        subclass(CollectionKey.FileInfo2::class, CollectionKey.FileInfo2.serializer())
    }
}

@Serializable
sealed interface CollectionKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.collection_title)

    override val icon get() = ComicIcons.CollectionsBookmark

    @Serializable
    data object List : CollectionKey

    @Serializable
    data class Detail(val id: CollectionId) : CollectionKey

    @Serializable
    data class CreateBasic(
        val bookshelfId: BookshelfId = BookshelfId(),
        val path: PathString = "",
    ) : CollectionKey

    @Serializable
    data class EditBasic(val collectionId: CollectionId) : CollectionKey

    @Serializable
    data class CreateSmart(
        val bookshelfId: BookshelfId? = null,
        val searchCondition: SearchCondition = SearchCondition(),
    ) : CollectionKey

    @Serializable
    data class EditSmart(val collectionId: CollectionId) : CollectionKey

    @Serializable
    data class Delete(val id: CollectionId) : CollectionKey

    @Serializable
    data class Folder(override val bookshelfId: BookshelfId, override val path: String) :
        CollectionKey,
        FolderKey {
        override val restorePath: String? = null
    }

    @Serializable
    data class FileInfo(override val fileKey: File.Key) :
        CollectionKey,
        FileInfoKey {
        override val isOpenFolderEnabled: Boolean = true
    }

    @Serializable
    data class FileInfo2(override val fileKey: File.Key) :
        CollectionKey,
        FileInfoKey {
        override val isOpenFolderEnabled: Boolean = true
    }
}

context(context: PlatformContext)
fun EntryProviderScope<NavKey>.collectionEntryGroup(
    navigator: Navigator,
    onSettingsClick: () -> Unit,
    onSearchClick: (BookshelfId, PathString) -> Unit,
    onCollectionBookClick: (Book, CollectionId) -> Unit,
    onBookClick: (Book) -> Unit,
) {
    collectionListEntry(
        onItemClick = {
            navigator.navigate(CollectionKey.Detail(it))
        },
        onEditClick = {
            val collection = when (it) {
                is BasicCollection -> CollectionKey.EditBasic(it.id)
                is SmartCollection -> CollectionKey.EditSmart(it.id)
            }
            navigator.navigate(collection)
        },
        onDeleteClick = { id ->
            navigator.navigate(CollectionKey.Delete(id))
        },
        onSettingsClick = onSettingsClick,
        onCreateBasicCollectionClick = {
            navigator.navigate(CollectionKey.CreateBasic())
        },
        onCreateSmartCollectionClick = {
            navigator.navigate(CollectionKey.CreateSmart())
        },
    )

    collectionCreateBasicEntry(
        onBackClick = navigator::goBack,
        onComplete = navigator::goBack,
    )
    collectionEditBasicEntry(
        onBackClick = navigator::goBack,
        onComplete = navigator::goBack,
    )

    collectionCreateSmartEntry(
        onCancelClick = navigator::goBack,
        onComplete = navigator::goBack,
    )
    collectionEditSmartEntry(
        onCancelClick = navigator::goBack,
        onComplete = navigator::goBack,
    )

    collectionDetailEntry(
        onBackClick = navigator::goBack,
        onFileClick = { file, collectionId ->
            when (file) {
                is Book -> {
                    onCollectionBookClick(file, collectionId)
                }

                is Folder -> {
                    navigator.navigate(
                        CollectionKey.Folder(file.bookshelfId, file.path),
                    )
                }
            }
        },
        onFileInfoClick = {
            navigator.navigate(CollectionKey.FileInfo2(it.key()))
        },
        onEditClick = { id ->
            // TODO
        },
        onDeleteClick = { id ->
            navigator.navigate(CollectionKey.Delete(id))
        },
        onSettingsClick = onSettingsClick,
    )

    collectionDeleteEntry(
        onBackClick = navigator::goBack,
        onComplete = navigator::goBack,
    )

    folderEntryGroup<CollectionKey.Folder, CollectionKey.FileInfo>(
        sceneKey = "Collection",
        onBackClick = navigator::goBack,
        onSearchClick = onSearchClick,
        onFileClick = { file ->
            when (file) {
                is Book -> {
                    onBookClick(file)
                }

                is Folder -> {
                    navigator.navigate(
                        CollectionKey.Folder(file.bookshelfId, file.path),
                    )
                }
            }
        },
        onFileInfoClick = {
            navigator.navigate(CollectionKey.FileInfo(it.key()))
        },
        onRestored = { /* Do noting */ },
        onCollectionClick = {},
        onSortClick = { sortType, folderScopeOnly ->
            navigator.navigate(SortTypeSelectKey(sortType, folderScopeOnly))
        },
        onSettingsClick = onSettingsClick,
        onOpenFolderClick = {
            navigator.navigate(CollectionKey.Folder(it.bookshelfId, it.path))
        },
    )

    fileInfoEntry<CollectionKey.FileInfo2>(
        "Collection",
        onBackClick = navigator::goBack,
        onCollectionClick = {},
        onOpenFolderClick = {
            navigator.navigate(CollectionKey.Folder(it.bookshelfId, it.path))
        },
    )
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.collectionListEntry(
    onItemClick: (CollectionId) -> Unit,
    onEditClick: (Collection) -> Unit,
    onDeleteClick: (CollectionId) -> Unit,
    onSettingsClick: () -> Unit,
    onCreateBasicCollectionClick: () -> Unit,
    onCreateSmartCollectionClick: () -> Unit,
) {
    entryScreen<CollectionKey.List, CollectionListScreenContext>(
        createContext = {
            context.require<CollectionListScreenContext.Factory>()
                .createCollectionListScreenContext()
        },
    ) {
        CollectionListScreenRoot(
            onItemClick = { collection -> onItemClick(collection.id) },
            onEditClick = { collection -> onEditClick(collection) },
            onDeleteClick = { collection -> onDeleteClick(collection.id) },
            onSettingsClick = onSettingsClick,
            onCreateBasicCollectionClick = onCreateBasicCollectionClick,
            onCreateSmartCollectionClick = onCreateSmartCollectionClick,
        )
    }
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.collectionCreateBasicEntry(
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    entryScreen<CollectionKey.CreateBasic, BasicCollectionCreateScreenContext>(
        createContext = {
            context.require<BasicCollectionCreateScreenContext.Factory>()
                .createBasicCollectionCreateScreenContext()
        },
        metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
    ) {
        BasicCollectionCreateScreenRoot(
            bookshelfId = it.bookshelfId,
            path = it.path,
            onBackClick = onBackClick,
            onComplete = onComplete,
        )
    }
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.collectionEditBasicEntry(
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    entryScreen<CollectionKey.EditBasic, BasicCollectionEditScreenContext>(
        createContext = {
            context.require<BasicCollectionEditScreenContext.Factory>()
                .createBasicCollectionEditScreenContext()
        },
        metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
    ) {
        BasicCollectionEditScreenRoot(
            collectionId = it.collectionId,
            onBackClick = onBackClick,
            onComplete = onComplete,
        )
    }
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.collectionCreateSmartEntry(
    onCancelClick: () -> Unit,
    onComplete: () -> Unit,
) {
    entryScreen<CollectionKey.CreateSmart, SmartCollectionCreateScreenContext>(
        createContext = {
            context.require<SmartCollectionCreateScreenContext.Factory>()
                .createSmartCollectionCreateScreenContext()
        },
        metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
    ) {
        SmartCollectionCreateScreenRoot(
            bookshelfId = it.bookshelfId,
            searchCondition = it.searchCondition,
            onCancelClick = onCancelClick,
            onComplete = onComplete,
        )
    }
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.collectionEditSmartEntry(
    onCancelClick: () -> Unit,
    onComplete: () -> Unit,
) {
    entryScreen<CollectionKey.EditSmart, SmartCollectionEditScreenContext>(
        createContext = {
            context.require<SmartCollectionEditScreenContext.Factory>()
                .createSmartCollectionEditScreenContext()
        },
        metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
    ) {
        SmartCollectionEditScreenRoot(
            collectionId = it.collectionId,
            onCancelClick = onCancelClick,
            onComplete = onComplete,
        )
    }
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.collectionDetailEntry(
    onBackClick: () -> Unit,
    onFileClick: (File, CollectionId) -> Unit,
    onFileInfoClick: (File) -> Unit,
    onEditClick: (CollectionId) -> Unit,
    onDeleteClick: (CollectionId) -> Unit,
    onSettingsClick: () -> Unit,
) {
    entryScreen<CollectionKey.Detail, CollectionScreenContext>(
        createContext = {
            context.require<CollectionScreenContext.Factory>().createCollectionScreenContext()
        },
        metadata = SupportingPaneSceneStrategy.mainPane("Collection"),
    ) { detail ->
        CollectionScreenRoot(
            id = detail.id,
            onBackClick = onBackClick,
            onFileClick = { onFileClick(it, detail.id) },
            onFileInfoClick = onFileInfoClick,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
            onSettingsClick = onSettingsClick,
        )
    }
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.collectionDeleteEntry(
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    entryScreen<CollectionKey.Delete, DeleteCollectionScreenContext>(
        createContext = {
            context.require<DeleteCollectionScreenContext.Factory>()
                .createDeleteCollectionScreenContext()
        },
        metadata = DialogSceneStrategy.dialog(),
    ) {
        DeleteCollectionScreenRoot(
            id = it.id,
            onBackClick = onBackClick,
            onComplete = onComplete,
        )
    }
}
