/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Settings(
    @ProtoNumber(1) val doneTutorial: Boolean = false,
    @ProtoNumber(2) val useAuth: Boolean = false,
    @ProtoNumber(3) val restoreOnLaunch: Boolean = false,
)
