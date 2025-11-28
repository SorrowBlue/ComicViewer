package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.feature.book.BookScreen
import com.sorrowblue.comicviewer.feature.book.BookScreenContext
import com.sorrowblue.comicviewer.feature.book.menu.BookMenuScreenContext
import com.sorrowblue.comicviewer.feature.book.menu.BookMenuScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val BookKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(BookKey::class, BookKey.serializer())
        subclass(BookMenuKey::class, BookMenuKey.serializer())
        subclass(ReceiveBookKey::class, ReceiveBookKey.serializer())
    }
}

@Serializable
data class ReceiveBookKey(val uri: String) : ScreenKey

@Serializable
data class BookKey(
    val bookshelfId: BookshelfId,
    val path: PathString,
    val name: String,
    val collectionId: CollectionId = CollectionId(),
) : ScreenKey

@Serializable
private data object BookMenuKey : ScreenKey

context(context: PlatformContext)
fun EntryProviderScope<NavKey>.bookEntryGroup(navigator: Navigator, onSettingsClick: () -> Unit) {
    bookEntry(
        onBackClick = navigator::goBack,
        onSettingsClick = onSettingsClick,
        onNextBookClick = { book, collectionId ->
            navigator.navigate(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                    collectionId = collectionId,
                ),
            )
        },
        onContainerLongClick = {
            navigator.navigate(BookMenuKey)
        },
    )
    bookMenuEntry(onDismissRequest = navigator::goBack)
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.bookEntry(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNextBookClick: (Book, CollectionId) -> Unit,
    onContainerLongClick: () -> Unit,
) {
    entryScreen<BookKey, BookScreenContext>(
        createContext = { context.require<BookScreenContext.Factory>().createBookScreenContext() },
    ) {
        BookScreen(
            bookshelfId = it.bookshelfId,
            path = it.path,
            name = it.name,
            collectionId = it.collectionId,
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick,
            onNextBookClick = onNextBookClick,
            onContainerLongClick = onContainerLongClick,
        )
    }
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.bookMenuEntry(onDismissRequest: () -> Unit) {
    entryScreen<BookMenuKey, BookMenuScreenContext>(
        createContext = {
            context.require<BookMenuScreenContext.Factory>().createBookMenuScreenContext()
        },
        metadata = DialogSceneStrategy.dialog(),
    ) {
        BookMenuScreenRoot(
            onDismissRequest = onDismissRequest,
        )
    }
}
