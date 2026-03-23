package com.sorrowblue.comicviewer.feature.settings.folder.di

import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderThumbnailOrderNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.ImageFilterQualityNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.ImageFormatNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.ImageScaleNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.SortTypeNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.folderSettingsNavEntry
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.folderThumbnailOrderNavEntry
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.imageFilterQualityNavEntry
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.imageFormatNavEntry
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.imageScaleNavEntry
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.sortTypeNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface FolderSettingsModule {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        FolderSettingsNavKey.serializer().asEntry(),
        FolderThumbnailOrderNavKey.serializer().asEntry(),
        ImageFilterQualityNavKey.serializer().asEntry(),
        ImageFormatNavKey.serializer().asEntry(),
        ImageScaleNavKey.serializer().asEntry(),
        SortTypeNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideFolderSettingsNavEntry(
        factory: FolderSettingsScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            folderSettingsNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideFolderThumbnailOrderNavEntry(): ScreenEntryProvider = { navigator ->
        folderThumbnailOrderNavEntry(navigator)
    }

    @Provides
    @IntoSet
    private fun provideImageFilterQualityNavEntry(): ScreenEntryProvider = { navigator ->
        imageFilterQualityNavEntry(navigator)
    }

    @Provides
    @IntoSet
    private fun provideImageFormatNavEntry(): ScreenEntryProvider = { navigator ->
        imageFormatNavEntry(navigator)
    }

    @Provides
    @IntoSet
    private fun provideImageScaleNavEntry(): ScreenEntryProvider = { navigator ->
        imageScaleNavEntry(navigator)
    }

    @Provides
    @IntoSet
    private fun provideSortTypeNavEntry(): ScreenEntryProvider = { navigator ->
        sortTypeNavEntry(navigator)
    }
}
