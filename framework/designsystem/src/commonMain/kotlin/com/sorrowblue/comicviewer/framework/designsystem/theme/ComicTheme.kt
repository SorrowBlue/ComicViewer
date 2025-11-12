package com.sorrowblue.comicviewer.framework.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object ComicTheme {
    val motionScheme: MotionScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.motionScheme
    val dimension: Dimension
        @Composable
        @ReadOnlyComposable
        get() {
            return LocalDimension.current
        }

    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    var fixedSegmentedButtonColorsCached: SegmentedButtonColors? = null
}
