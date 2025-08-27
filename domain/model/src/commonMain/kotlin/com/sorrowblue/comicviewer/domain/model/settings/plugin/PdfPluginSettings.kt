package com.sorrowblue.comicviewer.domain.model.settings.plugin

import kotlinx.serialization.Serializable

@Serializable
expect class PdfPluginSettings() {
    val isEnabled: Boolean
    val isInstallationChecked: Boolean
}
