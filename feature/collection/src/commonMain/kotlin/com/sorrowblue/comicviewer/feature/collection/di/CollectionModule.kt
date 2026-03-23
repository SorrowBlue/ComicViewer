package com.sorrowblue.comicviewer.feature.collection.di

import com.sorrowblue.comicviewer.feature.collection.CollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.list.CollectionListScreenContext
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionDeleteNavKey
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionFileInfoNavKey
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionFolderFileInfoNavKey
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionFolderNavKey
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionListNavKey
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionNavKey
import com.sorrowblue.comicviewer.feature.collection.navigation.collectionFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.collection.navigation.collectionFolderInfoNavEntry
import com.sorrowblue.comicviewer.feature.collection.navigation.collectionListNavEntry
import com.sorrowblue.comicviewer.feature.collection.navigation.collectionNavEntry
import com.sorrowblue.comicviewer.feature.collection.navigation.deleteCollectionNavEntry
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface CollectionModule {
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
    private fun provideNavigationKey(): NavigationKey = CollectionListNavKey

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
