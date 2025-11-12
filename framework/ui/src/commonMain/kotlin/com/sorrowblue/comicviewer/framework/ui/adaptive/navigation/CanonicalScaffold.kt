package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.runtime.compositionLocalOf

/** 境界が必要な場合はtrue */
internal data class CanonicalScaffoldBound(
    val start: Boolean = false,
    val top: Boolean = false,
    val end: Boolean = false,
    val bottom: Boolean = false,
)

internal val LocalCanonicalScaffoldBound = compositionLocalOf { CanonicalScaffoldBound() }
