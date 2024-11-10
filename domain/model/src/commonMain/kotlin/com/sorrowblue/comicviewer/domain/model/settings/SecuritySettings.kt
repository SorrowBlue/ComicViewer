package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.Serializable

@Serializable
data class SecuritySettings(
    val password: String? = null,
    val useBiometrics: Boolean = false,
    val lockOnBackground: Boolean = false,
)
