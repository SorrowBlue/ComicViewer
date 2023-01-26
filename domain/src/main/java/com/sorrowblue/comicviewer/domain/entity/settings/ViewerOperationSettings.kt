package com.sorrowblue.comicviewer.domain.entity.settings

import kotlinx.serialization.Serializable

@Serializable
data class ViewerOperationSettings(
    // 閉じ方向
    val bindingDirection: BindingDirection = BindingDirection.RTL,

    )
