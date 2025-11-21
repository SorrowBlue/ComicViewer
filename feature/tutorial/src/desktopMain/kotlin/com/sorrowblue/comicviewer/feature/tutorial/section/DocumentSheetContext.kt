package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.usecase.settings.ManagePdfPluginSettingsUseCase
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.scomicviewer.domain.usecase.RegisterPdfPluginUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope
import io.github.takahirom.rin.rememberRetained

@Scope
annotation class DocumentSheetScope

@GraphExtension(DocumentSheetScope::class)
interface DocumentSheetContext {
    val registerPdfPluginUseCase: RegisterPdfPluginUseCase
    val managePdfPluginSettingsUseCase: ManagePdfPluginSettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createDocumentSheetContext(): DocumentSheetContext
    }
}

@Composable
internal fun PlatformContext.rememberDocumentSheetContext() = rememberRetained {
    (this.require<DocumentSheetContext.Factory>()).createDocumentSheetContext()
}
