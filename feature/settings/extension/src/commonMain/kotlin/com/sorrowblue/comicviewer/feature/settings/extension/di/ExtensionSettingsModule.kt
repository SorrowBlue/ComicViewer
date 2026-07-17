/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.extension.di

import com.sorrowblue.comicviewer.feature.settings.extension.navigation.extensionSettingsNavEntry
import com.sorrowblue.comicviewer.feature.settings.extension.navigation.imageCacheNavEntry
import com.sorrowblue.comicviewer.feature.settings.extension.subscreen.imagecache.ImageCacheScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface ExtensionSettingsModule {
    @Provides
    @IntoSet
    private fun provideExtensionSettingsNavEntry(): ScreenEntryProvider = { navigator ->
        extensionSettingsNavEntry(navigator)
    }

    @Provides
    @IntoSet
    private fun provideImageCacheNavEntry(
        factory: ImageCacheScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            imageCacheNavEntry(navigator)
        }
    }
}
