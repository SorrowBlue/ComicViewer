package com.sorrowblue.comicviewer.domain.model.settings

import com.sorrowblue.comicviewer.domain.model.SupportExtension
import kotlinx.serialization.Serializable

@Serializable
data class FolderSettings(
    val isAutoRefresh: Boolean = true,
    val supportExtension: List<SupportExtension> = SupportExtension.Archive.entries,
    val resolveImageFolder: Boolean = false,
)
