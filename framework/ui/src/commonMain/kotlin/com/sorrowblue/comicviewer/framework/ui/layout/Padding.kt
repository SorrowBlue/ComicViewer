package com.sorrowblue.comicviewer.framework.ui.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.jvm.JvmInline

/**
 * Add the other [PaddingValues] to this [PaddingValues].
 *
 * @param other
 * @return
 */
@Composable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(layoutDirection) + other.calculateStartPadding(layoutDirection),
        top = calculateTopPadding() + other.calculateTopPadding(),
        end = calculateEndPadding(layoutDirection) + other.calculateEndPadding(layoutDirection),
        bottom = calculateBottomPadding() + other.calculateBottomPadding(),
    )
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
        bottom = calculateBottomPadding()
    )

@Composable
fun PaddingValues.copy(
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    start: Dp = calculateStartPadding(layoutDirection),
    top: Dp = calculateTopPadding(),
    end: Dp = calculateEndPadding(layoutDirection),
    bottom: Dp = calculateBottomPadding(),
) = PaddingValues(start = start, top = top, end = end, bottom = bottom)

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

    override fun calculateTopPadding(): Dp {
        return if (sides.hasAny(PaddingValuesSides.Top)) paddingValues.calculateTopPadding() else 0.dp
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

    override fun calculateBottomPadding(): Dp {
        return if (sides.hasAny(PaddingValuesSides.Bottom)) paddingValues.calculateBottomPadding() else 0.dp
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
