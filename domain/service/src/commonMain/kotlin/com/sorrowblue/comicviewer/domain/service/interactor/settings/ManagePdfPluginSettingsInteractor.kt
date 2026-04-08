package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.plugin.PdfPluginSettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.ManagePdfPluginSettingsUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

@ContributesBinding(DataScope::class)
internal class ManagePdfPluginSettingsInteractor(private val dataSource: DatastoreDataSource) :
    ManagePdfPluginSettingsUseCase {
    override val settings: Flow<PdfPluginSettings> = dataSource.pdfPluginSettings

    override suspend fun edit(action: (PdfPluginSettings) -> PdfPluginSettings) {
        dataSource.updatePdfPluginSettings(action)
    }
}
