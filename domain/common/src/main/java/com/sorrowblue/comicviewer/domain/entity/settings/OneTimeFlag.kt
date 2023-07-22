package com.sorrowblue.comicviewer.domain.entity.settings

import kotlinx.serialization.Serializable

@Serializable
data class OneTimeFlag(
    val isExplainNotificationPermission: Boolean = true
)
