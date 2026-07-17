/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.folder.di

import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.folderSettingsNavEntry
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.folderThumbnailOrderNavEntry
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.imageFilterQualityNavEntry
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.imageFormatNavEntry
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.imageScaleNavEntry
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.sortTypeNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface FolderSettingsModule {
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
