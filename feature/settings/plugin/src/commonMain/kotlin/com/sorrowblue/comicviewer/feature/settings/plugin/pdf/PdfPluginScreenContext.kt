package com.sorrowblue.comicviewer.feature.settings.plugin.pdf

import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class PdfPluginScreenScope

@GraphExtension(PdfPluginScreenScope::class)
expect interface PdfPluginScreenContext : ScreenContext {
    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createPdfPluginScreenContext(): PdfPluginScreenContext
    }
}
