/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * Window settings
 *
 * @property width Window width
 * @property height Window height
 * @property x Window x position
 * @property y Window y position
 * @property isMaximized Whether the window is maximized
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class WindowSettings(
    @ProtoNumber(1) val width: Int = 1024,
    @ProtoNumber(2) val height: Int = 768,
    @ProtoNumber(3) val x: Int = -1,
    @ProtoNumber(4) val y: Int = -1,
    @ProtoNumber(5) val isMaximized: Boolean = false,
)
