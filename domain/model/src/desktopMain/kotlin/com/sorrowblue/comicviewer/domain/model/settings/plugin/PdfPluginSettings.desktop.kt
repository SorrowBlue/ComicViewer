package com.sorrowblue.comicviewer.domain.model.settings.plugin

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
actual data class PdfPluginSettings(
    actual val isEnabled: Boolean = false,
    actual val isInstallationChecked: Boolean = false,
    val pluginRootPath: String = "",
    val pluginJarPath: String = "",
) {
    actual companion object {
        actual fun kSerializer(): KSerializer<PdfPluginSettings> = serializer()

        actual fun default() = PdfPluginSettings()
    }
}
