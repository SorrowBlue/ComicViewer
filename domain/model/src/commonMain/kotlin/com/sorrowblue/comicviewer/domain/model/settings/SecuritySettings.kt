/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * Application lock and authentication settings.
 *
 * @property password The passcode string, or `null` if passcode lock is disabled.
 * @property useBiometrics Whether biometric authentication is enabled.
 * @property lockOnBackground Whether to lock the application when moved to the background.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class SecuritySettings(
    @ProtoNumber(1) val password: String? = null,
    @ProtoNumber(2) val useBiometrics: Boolean = false,
    @ProtoNumber(3) val lockOnBackground: Boolean = false,
) {
    override fun toString(): String =
        "SecuritySettings(password=${if (password != null) "***" else "null"}, useBiometrics=$useBiometrics, lockOnBackground=$lockOnBackground)"
}
