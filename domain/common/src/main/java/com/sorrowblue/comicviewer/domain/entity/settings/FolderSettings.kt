package com.sorrowblue.comicviewer.domain.entity.settings

import com.sorrowblue.comicviewer.domain.model.SupportExtension
import kotlinx.serialization.Serializable

@Serializable
data class FolderSettings(
    val isAutoRefresh: Boolean = true,
    val supportExtension: Set<SupportExtension> = SupportExtension.Archive.entries.toSet(),
    val showPreview: Boolean = true,
    val resolveImageFolder: Boolean = false
)
