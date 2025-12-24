package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenContext
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
interface ReadLaterProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(ReadLaterNavKey.serializer()),
            toPair(ReadLaterFileInfoNavKey.serializer()),
            toPair(ReadLaterFolderNavKey.serializer()),
            toPair(ReadLaterFolderFileInfoNavKey.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideNavigationKey(): NavigationKey = ReadLaterNavKey

    @Provides
    @IntoSet
    private fun provideReadLaterNavEntry(
        factory: ReadLaterScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            readLaterNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideReadLaterFileInfoNavEntry(
        factory: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            readLaterFileInfoNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideReadLaterFolderFileInfoNavEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factoryFolder) {
            with(factoryFileInfo) {
                readLaterFolderFileInfoNavEntry(navigator)
            }
        }
    }
}
