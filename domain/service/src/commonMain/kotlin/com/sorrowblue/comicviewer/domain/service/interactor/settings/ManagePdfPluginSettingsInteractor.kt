package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.plugin.PdfPluginSettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.ManagePdfPluginSettingsUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
internal class ManagePdfPluginSettingsInteractor(
    private val dataSource: DatastoreDataSource,
) : ManagePdfPluginSettingsUseCase {
    override val settings: Flow<PdfPluginSettings> = dataSource.pdfPluginSettings

    override suspend fun edit(action: (PdfPluginSettings) -> PdfPluginSettings) {
        dataSource.updatePdfPluginSettings(action)
    }
}
