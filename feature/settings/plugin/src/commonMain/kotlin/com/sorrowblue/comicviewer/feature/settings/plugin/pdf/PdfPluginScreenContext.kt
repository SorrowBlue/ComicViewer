package com.sorrowblue.comicviewer.feature.settings.plugin.pdf

import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.Scope

@Scope
annotation class PdfPluginScreenScope

expect interface PdfPluginScreenContext : ScreenContext {
    fun interface Factory {
        fun createPdfPluginScreenContext(): PdfPluginScreenContext
    }
}
