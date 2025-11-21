package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Resource

abstract class GetPdfPluginStateUseCase :
    UseCase<EmptyRequest, PdfPluginState, Resource.SystemError>()

enum class PdfPluginState {
    Enable,
    OldVersion,
    NotInstalled,
}

const val PACKAGE_PDF_PLUGIN = "com.sorrowblue.comicviewer.plugin.pdf"
