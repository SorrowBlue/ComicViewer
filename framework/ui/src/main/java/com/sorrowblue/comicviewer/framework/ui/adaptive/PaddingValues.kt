package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalCanonicalScaffoldBound
import kotlin.math.max

@Composable
fun animateMainContentPaddingValues(
    ignore: Boolean = false,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    animationSpec: AnimationSpec<PaddingValues> = paddingValuesDefaultSpring,
    finishedListener: ((PaddingValues) -> Unit)? = null,
): State<PaddingValues> {
    val bound = LocalCanonicalScaffoldBound.current
    return animateValueAsState(
        targetValue = PaddingValues(
            start = if (ignore || !bound.start) 0.dp else ComicTheme.dimension.margin,
            top = if (ignore || !bound.top) 0.dp else ComicTheme.dimension.margin,
            end = if (ignore || !bound.end) 0.dp else ComicTheme.dimension.margin,
            bottom = if (ignore || !bound.bottom) 0.dp else ComicTheme.dimension.margin
        ),
        typeConverter = paddingValuesToVector(layoutDirection),
        animationSpec = animationSpec,
        label = "PaddingValuesAnimation",
        finishedListener = finishedListener
    )
}

private fun paddingValuesToVector(layoutDirection: LayoutDirection): TwoWayConverter<PaddingValues, AnimationVector4D> =
    TwoWayConverter(
        convertToVector = {
            AnimationVector4D(
                it.calculateStartPadding(layoutDirection).value,
                it.calculateTopPadding().value,
                it.calculateEndPadding(layoutDirection).value,
                it.calculateBottomPadding().value
            )
        },
        convertFromVector = {
            PaddingValues(
                max(it.v1, 0f).dp,
                max(it.v2, 0f).dp,
                max(it.v3, 0f).dp,
                max(it.v4, 0f).dp
            )
        }
    )

private val paddingValuesVisibilityThreshold = PaddingValues(1.dp)

private val paddingValuesDefaultSpring =
    spring(visibilityThreshold = paddingValuesVisibilityThreshold)
