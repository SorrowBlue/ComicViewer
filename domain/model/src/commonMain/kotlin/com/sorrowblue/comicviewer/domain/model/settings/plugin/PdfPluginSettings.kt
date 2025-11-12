package com.sorrowblue.comicviewer.domain.model.settings.plugin

import kotlinx.serialization.KSerializer

expect class PdfPluginSettings {
    val isEnabled: Boolean
    val isInstallationChecked: Boolean

    companion object {
        fun kSerializer(): KSerializer<PdfPluginSettings>

        fun default(): PdfPluginSettings
    }
}
