package com.sorrowblue.comicviewer.domain.entity.settings

import kotlinx.serialization.Serializable

@Serializable
data class ViewerOperationSettings(
    val bindingDirection: BindingDirection = BindingDirection.RTL
)
