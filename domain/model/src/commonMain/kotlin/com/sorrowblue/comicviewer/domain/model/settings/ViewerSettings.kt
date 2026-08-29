/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ViewerSettings(
    @ProtoNumber(1) val showStatusBar: Boolean = true,
    @ProtoNumber(2) val showNavigationBar: Boolean = true,
    @ProtoNumber(3) val keepOnScreen: Boolean = false,
    @ProtoNumber(4) val cutWhitespace: Boolean = false,
    @ProtoNumber(5) val enableBrightnessControl: Boolean = false,
    @ProtoNumber(6) val screenBrightness: Float = 0.3f,
    @ProtoNumber(7) val imageQuality: Int = 75,
    @ProtoNumber(8) val readAheadPageCount: Int = 3,
    @ProtoNumber(9) val bindingDirection: BindingDirection = BindingDirection.RTL,
    @ProtoNumber(10) val alwaysOpenFromFirstPage: Boolean = false,
    @ProtoNumber(11) val imageFormat: ImageFormat = ImageFormat.ORIGINAL,
)
