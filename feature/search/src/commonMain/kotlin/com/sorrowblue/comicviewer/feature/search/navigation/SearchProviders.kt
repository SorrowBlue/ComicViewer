package com.sorrowblue.comicviewer.feature.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.search.SearchScreenContext
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.folder.FolderScreenContext
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
interface SearchProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(SearchNavKey.serializer()),
            toPair(SearchFileInfoNavKey.serializer()),
            toPair(SearchFolderFileInfoNavKey.serializer()),
            toPair(SearchFolderNavKey.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideSearchNavEntry(
        factory: SearchScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = {
        with(factory) {
            searchNavEntry(it)
        }
    }

    @Provides
    @IntoSet
    private fun provideSearchFileInfoNavEntry(
        factory: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = {
        with(factory) {
            searchFileInfoNavEntry(it)
        }
    }

    @Provides
    @IntoSet
    private fun provideSearchFolderFileInfoNavEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = {
        with(factoryFolder) {
            with(factoryFileInfo) {
                searchFolderFileInfoNavEntry(it)
            }
        }
    }
}
