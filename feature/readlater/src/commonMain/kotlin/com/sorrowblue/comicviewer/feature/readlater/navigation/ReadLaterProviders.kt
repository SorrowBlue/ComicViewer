package com.sorrowblue.comicviewer.feature.readlater.navigation

import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenContext
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
interface ReadLaterProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        ReadLaterNavKey.serializer().asEntry(),
        ReadLaterFileInfoNavKey.serializer().asEntry(),
        ReadLaterFolderNavKey.serializer().asEntry(),
        ReadLaterFolderFileInfoNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideNavigationKey(): NavigationKey = ReadLaterNavKey

    @Provides
    @IntoSet
    private fun provideReadLaterNavEntry(
        factory: ReadLaterScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            readLaterNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideReadLaterFileInfoNavEntry(
        factory: FileInfoScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            readLaterFileInfoNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideReadLaterFolderFileInfoNavEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factoryFolder) {
            with(factoryFileInfo) {
                readLaterFolderFileInfoNavEntry(navigator)
            }
        }
    }
}
