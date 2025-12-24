package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.collection.CollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.list.CollectionListScreenContext
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
interface CollectionProviders {
    @Provides
    @IntoSet
    private fun provideNavigationKey(): NavigationKey = CollectionListNavKey

    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(CollectionListNavKey.serializer()),
            toPair(CollectionNavKey.serializer()),
            toPair(CollectionFileInfoNavKey.serializer()),
            toPair(CollectionFolderNavKey.serializer()),
            toPair(CollectionFolderFileInfoNavKey.serializer()),
            toPair(CollectionDeleteNavKey.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideCollectionListNavEntry(
        factory: CollectionListScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            collectionListNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideCollectionNavEntry(
        factory: CollectionScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) { collectionNavEntry(navigator) }
    }

    @Provides
    @IntoSet
    private fun provideCollectionFileInfoNavEntry(
        factory: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            collectionFileInfoNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideCollectionFolderInfoNavEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
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
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            deleteCollectionNavEntry(navigator)
        }
    }
}
