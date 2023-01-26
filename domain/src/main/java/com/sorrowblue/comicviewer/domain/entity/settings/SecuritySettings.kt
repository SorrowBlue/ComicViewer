package com.sorrowblue.comicviewer.domain.entity.settings

import kotlinx.serialization.Serializable

@Serializable
data class SecuritySettings(
    val password: String? = null,
    val useBiometrics: Boolean = false
)
