/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class SecuritySettings(
    @ProtoNumber(1) val password: String? = null,
    @ProtoNumber(2) val useBiometrics: Boolean = false,
    @ProtoNumber(3) val lockOnBackground: Boolean = false,
)
