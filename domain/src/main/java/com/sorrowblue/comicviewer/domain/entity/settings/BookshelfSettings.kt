package com.sorrowblue.comicviewer.domain.entity.settings

import com.sorrowblue.comicviewer.domain.model.SupportExtension
import kotlinx.serialization.Serializable

@Serializable
data class BookshelfSettings(
    val isAutoRefresh: Boolean = true,
    val supportExtension: Set<SupportExtension> = SupportExtension.values().toSet(),
    val showPreview: Boolean = true,
    val resolveImageFolder: Boolean = false
)
