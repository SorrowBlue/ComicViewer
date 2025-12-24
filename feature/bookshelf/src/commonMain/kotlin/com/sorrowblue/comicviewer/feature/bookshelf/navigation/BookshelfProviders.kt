package com.sorrowblue.comicviewer.feature.bookshelf.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfScreenContext
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.toPair
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@ContributesTo(AppScope::class)
interface BookshelfProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(BookshelfNavKey.serializer()),
            toPair(BookshelfFolderNavKey.serializer()),
            toPair(BookshelfFolderFileInfoNavKey.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideNavigationKey(): NavigationKey = BookshelfNavKey

    @Provides
    @IntoSet
    private fun provideBookshelfNavEntry(
        factory: BookshelfScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            bookshelfNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideBookshelfFolderFileInfoNavEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factoryFolder) {
            with(factoryFileInfo) {
                bookshelfFolderFileInfoNavEntry(navigator)
            }
        }
    }
}
