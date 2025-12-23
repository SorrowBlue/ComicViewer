package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.history.HistoryScreenContext
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
interface HistoryProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(HistoryNavKey.serializer()),
            toPair(HistoryFileInfoNavKey.serializer()),
            toPair(HistoryFolderFileInfoNavKey.serializer()),
            toPair(HistoryClearAllNavKey.serializer()),
            toPair(HistoryFolderNavKey.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideNavigationKey(): NavigationKey = HistoryNavKey

    @Provides
    @IntoSet
    private fun provideHistoryNavEntry(
        factory: HistoryScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = {
        with(factory) {
            historyNavEntry(it)
        }
    }

    @Provides
    @IntoSet
    private fun provideHistoryClearAllNavEntry(): EntryProviderScope<NavKey>.(Navigator) -> Unit =
        { historyClearAllNavEntry(it) }

    @Provides
    @IntoSet
    private fun provideHistoryFileInfoNavEntry(
        factory: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = {
        with(factory) {
            historyFileInfoNavEntry(it)
        }
    }

    @Provides
    @IntoSet
    private fun provideHistoryFolderFileInfoNavEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = {
        with(factoryFolder) {
            with(factoryFileInfo) {
                historyFolderFileInfoNavEntry(it)
            }
        }
    }
}
