package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.plugin.PdfPluginSettings

internal object PdfPluginSettingsSerializer :
    OkioKSerializer<PdfPluginSettings>(PdfPluginSettings.serializer()) {
    override val fileName = "pdfPluginSettings.pb"
    override val defaultValue = PdfPluginSettings()
}
