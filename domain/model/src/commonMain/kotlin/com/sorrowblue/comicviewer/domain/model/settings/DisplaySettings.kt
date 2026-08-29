/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class DisplaySettings(
    @ProtoNumber(1) val darkMode: DarkMode = DarkMode.DEVICE,
    @ProtoNumber(2) val restoreOnLaunch: Boolean = false,
)
