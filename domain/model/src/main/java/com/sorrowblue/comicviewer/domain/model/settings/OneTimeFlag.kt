package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.Serializable

@Serializable
data class OneTimeFlag(
    val isExplainNotificationPermission: Boolean = true,
)
