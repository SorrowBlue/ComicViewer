/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * Settings for the comic book viewer screen.
 *
 * @property showStatusBar Whether to display the system status bar while reading.
 * @property showNavigationBar Whether to display the system navigation bar while reading.
 * @property keepOnScreen Whether to keep the screen awake while reading.
 * @property cutWhitespace Whether to trim whitespace margins from page images.
 * @property enableBrightnessControl Whether custom screen brightness override is enabled.
 * @property screenBrightness The custom screen brightness level (`0.0f` to `1.0f`).
 * @property imageQuality The rendering quality percentage (`1` to `100`).
 * @property readAheadPageCount The number of subsequent pages to preload ahead.
 * @property bindingDirection The default reading direction.
 * @property alwaysOpenFromFirstPage Whether to always open books at the first page instead of resuming.
 * @property imageFormat The image decoding format used for viewer pages.
 */
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
