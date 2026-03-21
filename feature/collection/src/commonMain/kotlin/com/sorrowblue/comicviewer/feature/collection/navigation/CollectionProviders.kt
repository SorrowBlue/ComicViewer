package com.sorrowblue.comicviewer.feature.collection.navigation

import com.sorrowblue.comicviewer.feature.collection.CollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.list.CollectionListScreenContext
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface CollectionProviders {
    @Provides
    @IntoSet
    private fun provideNavigationKey(): NavigationKey = CollectionListNavKey

    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        CollectionListNavKey.serializer().asEntry(),
        CollectionNavKey.serializer().asEntry(),
        CollectionFileInfoNavKey.serializer().asEntry(),
        CollectionFolderNavKey.serializer().asEntry(),
        CollectionFolderFileInfoNavKey.serializer().asEntry(),
        CollectionDeleteNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideCollectionListNavEntry(
        factory: CollectionListScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            collectionListNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideCollectionNavEntry(
        factory: CollectionScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            collectionNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideCollectionFileInfoNavEntry(
        factory: FileInfoScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            collectionFileInfoNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideCollectionFolderInfoNavEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factoryFolder) {
            with(factoryFileInfo) {
                collectionFolderInfoNavEntry(navigator)
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideCollectionDeleteNavEntry(
        factory: DeleteCollectionScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            deleteCollectionNavEntry(navigator)
        }
    }
}
