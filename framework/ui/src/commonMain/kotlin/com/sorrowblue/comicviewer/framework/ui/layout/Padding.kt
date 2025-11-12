package com.sorrowblue.comicviewer.framework.ui.layout

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalCanonicalScaffoldBound
import kotlin.jvm.JvmInline
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

/**
 * Add the other [PaddingValues] to this [PaddingValues].
 *
 * @param other
 * @return
 */
@Composable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues =
    AddedPaddingValues(this, other)

@Immutable
private class AddedPaddingValues(val first: PaddingValues, val second: PaddingValues) :
    PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        first.calculateLeftPadding(layoutDirection) +
            second.calculateLeftPadding(layoutDirection)

    override fun calculateTopPadding(): Dp =
        first.calculateTopPadding() + second.calculateTopPadding()

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        first.calculateRightPadding(layoutDirection) +
            second.calculateRightPadding(layoutDirection)

    override fun calculateBottomPadding(): Dp =
        first.calculateBottomPadding() + second.calculateBottomPadding()

    override fun equals(other: Any?): Boolean {
        if (other !is AddedPaddingValues) return false
        return first == other.first && second == other.second
    }

    override fun hashCode() = first.hashCode() * 31 + second.hashCode()

    override fun toString() = "($first + $second)"
}

/**
 * [PaddingValues] を [WindowInsets] に変換します。
 *
 * @param layoutDirection LayoutDirection
 */
@Composable
fun PaddingValues.asWindowInsets(layoutDirection: LayoutDirection = LocalLayoutDirection.current) =
    WindowInsets(
        left = calculateLeftPadding(layoutDirection),
        top = calculateTopPadding(),
        right = calculateRightPadding(layoutDirection),
        bottom = calculateBottomPadding(),
    )

@Composable
fun PaddingValues.copy(
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    start: Dp = calculateStartPadding(layoutDirection),
    top: Dp = calculateTopPadding(),
    end: Dp = calculateEndPadding(layoutDirection),
    bottom: Dp = calculateBottomPadding(),
) = PaddingValues(start = start, top = top, end = end, bottom = bottom)

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
            bottom = if (ignore || !bound.bottom) 0.dp else ComicTheme.dimension.margin,
        ),
        typeConverter = paddingValuesToVector(layoutDirection),
        animationSpec = animationSpec,
        label = "PaddingValuesAnimation",
        finishedListener = finishedListener,
    )
}

@Composable
fun animatePaddingValues(
    value: PaddingValues,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    animationSpec: AnimationSpec<PaddingValues> = paddingValuesDefaultSpring,
    finishedListener: ((PaddingValues) -> Unit)? = null,
): State<PaddingValues> = animateValueAsState(
    targetValue = value.copy(),
    typeConverter = paddingValuesToVector(layoutDirection),
    animationSpec = animationSpec,
    label = "PaddingValuesAnimation",
    finishedListener = finishedListener,
)

fun paddingValuesToVector(
    layoutDirection: LayoutDirection,
): TwoWayConverter<PaddingValues, AnimationVector4D> = TwoWayConverter(
    convertToVector = {
        AnimationVector4D(
            it.calculateStartPadding(layoutDirection).value,
            it.calculateTopPadding().value,
            it.calculateEndPadding(layoutDirection).value,
            it.calculateBottomPadding().value,
        )
    },
    convertFromVector = {
        PaddingValues(
            max(it.v1, 0f).dp,
            max(it.v2, 0f).dp,
            max(it.v3, 0f).dp,
            max(it.v4, 0f).dp,
        )
    },
)

private val ZeroDP = 0.dp

private val paddingValuesVisibilityThreshold = PaddingValues(1.dp)

private val paddingValuesDefaultSpring =
    spring(visibilityThreshold = paddingValuesVisibilityThreshold)

@JvmInline
value class PaddingValuesSides private constructor(private val value: Int) {
    /** Returns a [PaddingValuesSides] containing sides defied in [sides] and the sides in `this`. */
    operator fun plus(sides: PaddingValuesSides): PaddingValuesSides =
        PaddingValuesSides(value or sides.value)

    internal fun hasAny(sides: PaddingValuesSides): Boolean = (value and sides.value) != 0

    override fun toString(): String = "PaddingValuesSides(${valueToString()})"

    private fun valueToString(): String = buildString {
        fun appendPlus(text: String) {
            if (isNotEmpty()) append('+')
            append(text)
        }

        if (value and Start.value == Start.value) appendPlus("Start")
        if (value and Left.value == Left.value) appendPlus("Left")
        if (value and Top.value == Top.value) appendPlus("Top")
        if (value and End.value == End.value) appendPlus("End")
        if (value and Right.value == Right.value) appendPlus("Right")
        if (value and Bottom.value == Bottom.value) appendPlus("Bottom")
    }

    companion object {
        internal val AllowLeftInLtr = PaddingValuesSides(1 shl 3)
        internal val AllowRightInLtr = PaddingValuesSides(1 shl 2)
        internal val AllowLeftInRtl = PaddingValuesSides(1 shl 1)
        internal val AllowRightInRtl = PaddingValuesSides(1 shl 0)

        val Start = AllowLeftInLtr + AllowRightInRtl

        val End = AllowRightInLtr + AllowLeftInRtl

        val Top = PaddingValuesSides(1 shl 4)

        val Bottom = PaddingValuesSides(1 shl 5)

        val Left = AllowLeftInLtr + AllowLeftInRtl

        val Right = AllowRightInLtr + AllowRightInRtl

        val Horizontal = Left + Right

        val Vertical = Top + Bottom
    }
}

fun PaddingValues.only(sides: PaddingValuesSides): PaddingValues = LimitPaddingValues(this, sides)

@Stable
private class LimitPaddingValues(val paddingValues: PaddingValues, val sides: PaddingValuesSides) :
    PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp {
        val layoutDirectionSide =
            if (layoutDirection == LayoutDirection.Ltr) {
                PaddingValuesSides.AllowLeftInLtr
            } else {
                PaddingValuesSides.AllowLeftInRtl
            }
        val allowLeft = sides.hasAny(layoutDirectionSide)
        return if (allowLeft) {
            paddingValues.calculateLeftPadding(layoutDirection)
        } else {
            0.dp
        }
    }

    override fun calculateTopPadding(): Dp = if (sides.hasAny(
            PaddingValuesSides.Top,
        )
    ) {
        paddingValues.calculateTopPadding()
    } else {
        0.dp
    }

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp {
        val layoutDirectionSide =
            if (layoutDirection == LayoutDirection.Ltr) {
                PaddingValuesSides.AllowRightInLtr
            } else {
                PaddingValuesSides.AllowRightInRtl
            }
        val allowRight = sides.hasAny(layoutDirectionSide)
        return if (allowRight) {
            paddingValues.calculateRightPadding(layoutDirection)
        } else {
            0.dp
        }
    }

    override fun calculateBottomPadding(): Dp = if (sides.hasAny(
            PaddingValuesSides.Bottom,
        )
    ) {
        paddingValues.calculateBottomPadding()
    } else {
        0.dp
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is LimitPaddingValues) {
            return false
        }
        return paddingValues == other.paddingValues && sides == other.sides
    }

    override fun hashCode(): Int {
        var result = paddingValues.hashCode()
        result = 31 * result + sides.hashCode()
        return result
    }

    override fun toString(): String = "($paddingValues only $sides)"
}
