package com.sorrowblue.comicviewer.domain.model.settings.plugin

import kotlinx.serialization.Serializable

@Serializable
actual data class PdfPluginSettings(
    actual val isEnabled: Boolean = false,
    actual val isInstallationChecked: Boolean = false,
)
