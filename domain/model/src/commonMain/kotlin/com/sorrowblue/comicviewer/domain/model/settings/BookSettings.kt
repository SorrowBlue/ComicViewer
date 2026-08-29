/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class BookSettings(
    @ProtoNumber(1) val pageFormat: PageFormat = PageFormat.Default,
    @ProtoNumber(2) val pageScale: PageScale = PageScale.Fit,
) {
    @Serializable
    enum class PageFormat {
        Default,
        Spread,
        Split,
        Auto,
    }

    @Serializable
    enum class PageScale {
        Fit,
        FillWidth,
        FillHeight,
        Inside,
        None,
        FillBounds,
    }
}
