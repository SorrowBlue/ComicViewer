/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * Per-book or default page layout and scaling settings.
 *
 * @property pageFormat The page display layout format.
 * @property pageScale The page scaling mode within the viewer viewport.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class BookSettings(
    @ProtoNumber(1) val pageFormat: PageFormat = PageFormat.Default,
    @ProtoNumber(2) val pageScale: PageScale = PageScale.Fit,
) {
    /**
     * Represents the page display layout format in the viewer.
     */
    @Serializable
    enum class PageFormat {
        /** Display one page at a time as-is. */
        Default,

        /** Combine adjacent portrait pages into a two-page spread. */
        Spread,

        /** Split landscape pages into left and right halves. */
        Split,

        /** Automatically choose between spread and split depending on orientation. */
        Auto,
    }

    /**
     * Represents how a page image is scaled to fit the viewer viewport.
     */
    @Serializable
    enum class PageScale {
        /** Scale the image uniformly to fit within the viewport. */
        Fit,

        /** Scale the image to fill the viewport width. */
        FillWidth,

        /** Scale the image to fill the viewport height. */
        FillHeight,

        /** Scale the image down if larger than the viewport, otherwise keep original size. */
        Inside,

        /** Do not scale the image. */
        None,

        /** Stretch the image non-uniformly to fill the viewport bounds. */
        FillBounds,
    }
}
