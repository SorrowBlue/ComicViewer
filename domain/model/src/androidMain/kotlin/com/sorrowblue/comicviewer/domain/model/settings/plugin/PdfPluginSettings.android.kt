package com.sorrowblue.comicviewer.domain.model.settings.plugin

import kotlinx.serialization.Serializable

/**
 * PDFプラグインの設定
 *
 * @property isEnabled 有効設定かどうか
 * @property isInstallationChecked インストールをチェックしたかどうか
 */
@Serializable
actual data class PdfPluginSettings(
    actual val isEnabled: Boolean = false,
    actual val isInstallationChecked: Boolean = false,
)
