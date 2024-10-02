package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

fun WindowInsets.nonZero(insets: WindowInsets): WindowInsets = NonZeroInsets(this, insets)

@Stable
private class NonZeroInsets(
    private val first: WindowInsets,
    private val second: WindowInsets,
) : WindowInsets {
    override fun getLeft(density: Density, layoutDirection: LayoutDirection) =
        first.getLeft(density, layoutDirection).let {
            if (it > 0) it else second.getLeft(density, layoutDirection)
        }

    override fun getTop(density: Density) =
        first.getTop(density).let {
            if (it > 0) it else second.getTop(density)
        }

    override fun getRight(density: Density, layoutDirection: LayoutDirection) =
        first.getRight(density, layoutDirection).let {
            if (it > 0) it else second.getRight(density, layoutDirection)
        }

    override fun getBottom(density: Density) = first.getBottom(density).let {
        if (it > 0) it else second.getBottom(density)
    }

    override fun hashCode(): Int = first.hashCode() + second.hashCode() * 31

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is NonZeroInsets) {
            return false
        }
        return other.first == first && other.second == second
    }

    override fun toString(): String = "($first Z $second)"
}