package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.Serializable

@Serializable
data class BookSettings(
    val pageFormat: PageFormat = PageFormat.Default,
    val pageScale: PageScale = PageScale.Fit,
) {

    enum class PageFormat {
        Default,
        Spread,
        Split,
        Auto,
    }

    enum class PageScale {
        Fit,
        FillWidth,
        FillHeight,
        Inside,
        None,
        FillBounds,
    }
}
