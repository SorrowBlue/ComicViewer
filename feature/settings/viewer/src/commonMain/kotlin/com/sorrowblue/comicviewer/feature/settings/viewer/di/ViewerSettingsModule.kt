/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.viewer.di

import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.bindingDirectionNavEntry
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.viewerSettingsNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface ViewerSettingsModule {
    @Provides
    @IntoSet
    private fun provideViewerSettingsEntry(
        factory: ViewerSettingsScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            viewerSettingsNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideBindingDirectionEntry(): ScreenEntryProvider = { navigator ->
        bindingDirectionNavEntry(navigator)
    }
}
