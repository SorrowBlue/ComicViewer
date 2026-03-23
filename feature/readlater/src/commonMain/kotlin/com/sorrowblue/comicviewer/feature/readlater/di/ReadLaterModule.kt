package com.sorrowblue.comicviewer.feature.readlater.di

import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenContext
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterFileInfoNavKey
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterFolderFileInfoNavKey
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterFolderNavKey
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavKey
import com.sorrowblue.comicviewer.feature.readlater.navigation.readLaterFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.readlater.navigation.readLaterFolderFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.readlater.navigation.readLaterNavEntry
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
interface ReadLaterModule {
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
