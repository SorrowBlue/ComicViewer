package com.sorrowblue.comicviewer.feature.settings.plugin.pdf

import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension

@GraphExtension(scope = PdfPluginScreenScope::class)
actual interface PdfPluginScreenContext : ScreenContext {
    @ContributesTo(scope = AppScope::class)
    @GraphExtension.Factory
    actual fun interface Factory {
        actual fun createPdfPluginScreenContext(): PdfPluginScreenContext
    }
}
