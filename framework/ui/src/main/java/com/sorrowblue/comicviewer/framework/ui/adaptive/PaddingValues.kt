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
import kotlin.math.max

@Composable
fun PaddingValues.copyWhenZero(
    skipStart: Boolean = false,
    skipTop: Boolean = false,
    skipEnd: Boolean = false,
    skipBottom: Boolean = false,
): PaddingValues {
    val start = calculateStartPadding(LocalLayoutDirection.current).let { startPadding ->
        when {
            skipStart -> startPadding
            startPadding > ZeroDP -> startPadding
            else -> ComicTheme.dimension.margin
        }
    }
    val top = calculateTopPadding().let { topPadding ->
        when {
            skipTop -> topPadding
            topPadding > ZeroDP -> topPadding
            else -> ComicTheme.dimension.margin
        }
    }
    val end = calculateEndPadding(LocalLayoutDirection.current).let { endPadding ->
        when {
            skipEnd -> endPadding
            endPadding > ZeroDP -> endPadding
            else -> ComicTheme.dimension.margin
        }
    }
    val bottom = calculateBottomPadding().let { bottomPadding ->
        when {
            skipBottom -> bottomPadding
            bottomPadding > ZeroDP -> bottomPadding
            else -> ComicTheme.dimension.margin
        }
    }
    return PaddingValues(start, top, end, bottom)
}

@Composable
fun animatePaddingValuesAsState(
    targetValue: PaddingValues,
    animationSpec: AnimationSpec<PaddingValues> = paddingValuesDefaultSpring,
    label: String = "PaddingValuesAnimation",
    finishedListener: ((PaddingValues) -> Unit)? = null,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
): State<PaddingValues> {
    return animateValueAsState(
        targetValue,
        paddingValuesToVector(layoutDirection),
        animationSpec,
        label = label,
        finishedListener = finishedListener
    )
}

@Composable
fun animateMainContentPaddingValues(ignore: Boolean = false): State<PaddingValues> {
    val bound = LocalCanonicalScaffoldBound.current
    return animatePaddingValuesAsState(
        PaddingValues(
            start = if (ignore || !bound.start) 0.dp else ComicTheme.dimension.margin,
            top = if (ignore || !bound.top) 0.dp else ComicTheme.dimension.margin,
            end = if (ignore || !bound.end) 0.dp else ComicTheme.dimension.margin,
            bottom = if (ignore || !bound.bottom) 0.dp else ComicTheme.dimension.margin
        )
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

private val ZeroDP = 0.dp

private val paddingValuesVisibilityThreshold = PaddingValues(1.dp)

private val paddingValuesDefaultSpring =
    spring(visibilityThreshold = paddingValuesVisibilityThreshold)
