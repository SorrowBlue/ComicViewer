package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.SupportExtension.Document
import com.sorrowblue.comicviewer.domain.service.datasource.DocumentReaderDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DocumentReaderState
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManagePdfPluginSettingsUseCase
import com.sorrowblue.scomicviewer.domain.usecase.RegisterPdfPluginUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Inject
internal class RegisterPdfPluginInteractor(
    private val managePdfPluginSettingsUseCase: ManagePdfPluginSettingsUseCase,
    private val manageFolderSettingsUseCase: ManageFolderSettingsUseCase,
    private val pdfPluginDataSource: DocumentReaderDataSource,
) : RegisterPdfPluginUseCase() {
    override fun run(request: Request): Flow<Resource<Result, Error>> = flow {
        val state =
            pdfPluginDataSource.initializePdfPlugin(request.rootPath)
        when (state) {
            DocumentReaderState.Success -> {
                managePdfPluginSettingsUseCase.edit {
                    it.copy(pluginRootPath = request.rootPath)
                }
                updateSupportExtension(true)
                updatePdfPluginState(true)
                emit(Resource.Success(Result(pdfPluginDataSource.version)))
            }

            is DocumentReaderState.InvalidVersion -> {
                emit(Resource.Error(Error.NotSupportVersion))
            }

            DocumentReaderState.Error -> {
                emit(Resource.Error(Error.NotFound))
            }

            DocumentReaderState.NotFound -> {
                emit(Resource.Error(Error.NotFound))
            }
        }
    }

    private suspend fun updateSupportExtension(isSupportDocument: Boolean) {
        manageFolderSettingsUseCase.edit { settings ->
            val supportedException = if (isSupportDocument) {
                settings.supportExtension.plus(Document.entries)
            } else {
                settings.supportExtension.minus(Document.entries)
            }
            settings.copy(supportExtension = supportedException)
        }
    }

    private suspend fun updatePdfPluginState(isEnabled: Boolean) {
        managePdfPluginSettingsUseCase.edit { settings ->
            settings.copy(isEnabled = isEnabled)
        }
    }
}
