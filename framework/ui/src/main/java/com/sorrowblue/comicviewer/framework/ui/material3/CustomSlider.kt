package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinish: (() -> Unit)? = null,
    colors: SliderColors = CustomSliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    showIndicator: Boolean = false,
    showLabel: Boolean = false,
    thumbLabel: (Float) -> String = Float::toString,
    thumb: @Composable (thumbValue: Float) -> Unit = {
        CustomSliderDefaults.Thumb(
            thumbValue = thumbLabel(it),
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled
        )
    },
    track: @Composable (sliderState: SliderState) -> Unit = { sliderState ->
        CustomSliderDefaults.Track(
            colors = colors,
            enabled = enabled,
            sliderState = sliderState
        )
    },
    indicator: @Composable (indicatorValue: Float) -> Unit = { indicatorValue ->
        CustomSliderDefaults.Indicator(
            indicatorValue = indicatorValue.toString(),
            modifier = Modifier.layoutId(CustomSliderComponents.INDICATOR)
        )
    },
    label: @Composable (labelValue: Float) -> Unit = { labelValue ->
        CustomSliderDefaults.Label(labelValue = labelValue.toString())
    },
    steps: Int = 0,
) {
    val thumbContent = remember { movableContentOf { thumb(value) } }
    val itemCount = (valueRange.endInclusive - valueRange.start).roundToInt()
    Box(modifier = modifier) {
        Layout(
            measurePolicy = customSliderMeasurePolicy(
                itemCount = itemCount,
                gap = steps + 2,
                value = value,
                startValue = valueRange.start
            ),
            content = {
                if (showLabel) {
                    Label(
                        modifier = Modifier.layoutId(CustomSliderComponents.LABEL),
                        value = value,
                        label = label
                    )
                }

                Box(modifier = Modifier.layoutId(CustomSliderComponents.THUMB)) {
                    thumbContent()
                }

                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .layoutId(CustomSliderComponents.SLIDER),
                    enabled = enabled,
                    valueRange = valueRange,
                    onValueChangeFinished = onValueChangeFinish,
                    colors = colors.toOriginal(),
                    steps = steps,
                    thumb = { thumbContent() },
                    track = { track(it) }
                )

                if (showIndicator) {
                    Indicator(
                        valueRange = valueRange,
                        gap = steps + 2,
                        indicator = indicator
                    )
                }
            }
        )
    }
}

private enum class CustomSliderComponents {
    SLIDER, LABEL, INDICATOR, THUMB
}

private fun customSliderMeasurePolicy(
    itemCount: Int,
    gap: Int,
    value: Float,
    startValue: Float,
) = MeasurePolicy { measurables, constraints ->
    // Measure the thumb component and calculate its radius.
    val thumbPlaceable = measurables.first {
        it.layoutId == CustomSliderComponents.THUMB
    }.measure(constraints)
    val thumbRadius = (thumbPlaceable.width / 2).toFloat()

    val indicatorPlaceables = measurables.filter {
        it.layoutId == CustomSliderComponents.INDICATOR
    }.map { measurable ->
        measurable.measure(constraints)
    }
    val indicatorHeight = indicatorPlaceables.maxByOrNull { it.height }?.height ?: 0

    val sliderPlaceable = measurables.first {
        it.layoutId == CustomSliderComponents.SLIDER
    }.measure(constraints)
    val sliderHeight = sliderPlaceable.height

    val labelPlaceable = measurables.find {
        it.layoutId == CustomSliderComponents.LABEL
    }?.measure(constraints)
    val labelHeight = labelPlaceable?.height ?: 0

    // Calculate the total width and height of the custom slider layout
    val width = sliderPlaceable.width
    val height = labelHeight + sliderHeight + indicatorHeight

    // Calculate the available width for the track (excluding thumb radius on both sides).
    val trackWidth = width - (2 * thumbRadius)

    // Calculate the width of each section in the track.
    val sectionWidth = trackWidth / itemCount
    // Calculate the horizontal spacing between indicators.
    val indicatorSpacing = sectionWidth * gap

    // To calculate offset of the label, first we will calculate the progress of the slider
    // by subtracting startValue from the current value.
    // After that we will multiply this progress by the sectionWidth.
    // Add thumb radius to this resulting value.
    val labelOffset = (sectionWidth * (value - startValue)) + thumbRadius

    layout(width = width, height = height) {
        var indicatorOffsetX = thumbRadius
        // Place label at top.
        // We have to subtract the half width of the label from the labelOffset,
        // to place our label at the center.
        labelPlaceable?.placeRelative(
            x = (labelOffset - (labelPlaceable.width / 2)).roundToInt(),
            y = 0
        )

        // Place slider placeable below the label.
        sliderPlaceable.placeRelative(x = 0, y = labelHeight)

        // Place indicators below the slider.
        indicatorPlaceables.forEach { placeable ->
            // We have to subtract the half width of the each indicator from the indicatorOffset,
            // to place our indicators at the center.
            placeable.placeRelative(
                x = (indicatorOffsetX - (placeable.width / 2)).roundToInt(),
                y = labelHeight + sliderHeight
            )
            indicatorOffsetX += indicatorSpacing
        }
    }
}

@Composable
private fun Label(
    value: Float,
    label: @Composable (labelValue: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        label(value)
    }
}

@Composable
private fun Indicator(
    valueRange: ClosedFloatingPointRange<Float>,
    gap: Int,
    indicator: @Composable (indicatorValue: Float) -> Unit,
) {
    // Iterate over the value range and display indicators at regular intervals.
    for (i in valueRange.start.roundToInt()..valueRange.endInclusive.roundToInt() step gap) {
        indicator(i.toFloat())
    }
}

internal enum class ColorSchemeKeyTokens {
    Background,
    Error,
    ErrorContainer,
    InverseOnSurface,
    InversePrimary,
    InverseSurface,
    OnBackground,
    OnError,
    OnErrorContainer,
    OnPrimary,
    OnPrimaryContainer,
    OnSecondary,
    OnSecondaryContainer,
    OnSurface,
    OnSurfaceVariant,
    OnTertiary,
    OnTertiaryContainer,
    Outline,
    OutlineVariant,
    Primary,
    PrimaryContainer,
    Scrim,
    Secondary,
    SecondaryContainer,
    Surface,
    SurfaceTint,
    SurfaceVariant,
    Tertiary,
    TertiaryContainer,
}

internal enum class ShapeKeyTokens {
    CornerExtraLarge,
    CornerExtraLargeTop,
    CornerExtraSmall,
    CornerExtraSmallTop,
    CornerFull,
    CornerLarge,
    CornerLargeEnd,
    CornerLargeTop,
    CornerMedium,
    CornerNone,
    CornerSmall,
}

object ElevationTokens {
    val Level0 = 0.0.dp
    val Level1 = 1.0.dp
    val Level2 = 3.0.dp
    val Level3 = 6.0.dp
    val Level4 = 8.0.dp
    val Level5 = 12.0.dp
}

internal enum class TypographyKeyTokens {
    BodyLarge,
    BodyMedium,
    BodySmall,
    DisplayLarge,
    DisplayMedium,
    DisplaySmall,
    HeadlineLarge,
    HeadlineMedium,
    HeadlineSmall,
    LabelLarge,
    LabelMedium,
    LabelSmall,
    TitleLarge,
    TitleMedium,
    TitleSmall,
}

internal object SliderTokens {
    val ActiveTrackColor = ColorSchemeKeyTokens.Primary
    val ActiveTrackHeight = 4.0.dp
    val ActiveTrackShape = ShapeKeyTokens.CornerFull
    val DisabledActiveTrackColor = ColorSchemeKeyTokens.OnSurface
    const val DisabledActiveTrackOpacity = 0.38f
    val DisabledHandleColor = ColorSchemeKeyTokens.OnSurface
    val DisabledHandleElevation = ElevationTokens.Level0
    const val DisabledHandleOpacity = 0.38f
    val DisabledInactiveTrackColor = ColorSchemeKeyTokens.OnSurface
    const val DisabledInactiveTrackOpacity = 0.12f
    val FocusHandleColor = ColorSchemeKeyTokens.Primary
    val HandleColor = ColorSchemeKeyTokens.Primary
    val HandleElevation = ElevationTokens.Level1
    val HandleHeight = 20.0.dp
    val HandleShape = ShapeKeyTokens.CornerFull
    val HandleWidth = 20.0.dp
    val HoverHandleColor = ColorSchemeKeyTokens.Primary
    val InactiveTrackColor = ColorSchemeKeyTokens.SurfaceVariant
    val InactiveTrackHeight = 4.0.dp
    val InactiveTrackShape = ShapeKeyTokens.CornerFull
    val LabelContainerColor = ColorSchemeKeyTokens.Primary
    val LabelContainerElevation = ElevationTokens.Level0
    val LabelContainerHeight = 28.0.dp
    val LabelTextColor = ColorSchemeKeyTokens.OnPrimary
    val LabelTextFont = TypographyKeyTokens.LabelMedium
    val PressedHandleColor = ColorSchemeKeyTokens.Primary
    val StateLayerSize = 40.0.dp
    val TrackElevation = ElevationTokens.Level0
    val OverlapHandleOutlineColor = ColorSchemeKeyTokens.OnPrimary
    val OverlapHandleOutlineWidth = 1.0.dp
    val TickMarksActiveContainerColor = ColorSchemeKeyTokens.OnPrimary
    const val TickMarksActiveContainerOpacity = 0.38f
    val TickMarksContainerShape = ShapeKeyTokens.CornerFull
    val TickMarksContainerSize = 2.0.dp
    val TickMarksDisabledContainerColor = ColorSchemeKeyTokens.OnSurface
    const val TickMarksDisabledContainerOpacity = 0.38f
    val TickMarksInactiveContainerColor = ColorSchemeKeyTokens.OnSurfaceVariant
    const val TickMarksInactiveContainerOpacity = 0.38f
}

@ReadOnlyComposable
@Composable
internal fun ColorSchemeKeyTokens.toColor(): Color {
    return MaterialTheme.colorScheme.fromToken(this)
}

internal fun ColorScheme.fromToken(value: ColorSchemeKeyTokens): Color {
    return when (value) {
        ColorSchemeKeyTokens.Background -> background
        ColorSchemeKeyTokens.Error -> error
        ColorSchemeKeyTokens.ErrorContainer -> errorContainer
        ColorSchemeKeyTokens.InverseOnSurface -> inverseOnSurface
        ColorSchemeKeyTokens.InversePrimary -> inversePrimary
        ColorSchemeKeyTokens.InverseSurface -> inverseSurface
        ColorSchemeKeyTokens.OnBackground -> onBackground
        ColorSchemeKeyTokens.OnError -> onError
        ColorSchemeKeyTokens.OnErrorContainer -> onErrorContainer
        ColorSchemeKeyTokens.OnPrimary -> onPrimary
        ColorSchemeKeyTokens.OnPrimaryContainer -> onPrimaryContainer
        ColorSchemeKeyTokens.OnSecondary -> onSecondary
        ColorSchemeKeyTokens.OnSecondaryContainer -> onSecondaryContainer
        ColorSchemeKeyTokens.OnSurface -> onSurface
        ColorSchemeKeyTokens.OnSurfaceVariant -> onSurfaceVariant
        ColorSchemeKeyTokens.SurfaceTint -> surfaceTint
        ColorSchemeKeyTokens.OnTertiary -> onTertiary
        ColorSchemeKeyTokens.OnTertiaryContainer -> onTertiaryContainer
        ColorSchemeKeyTokens.Outline -> outline
        ColorSchemeKeyTokens.OutlineVariant -> outlineVariant
        ColorSchemeKeyTokens.Primary -> primary
        ColorSchemeKeyTokens.PrimaryContainer -> primaryContainer
        ColorSchemeKeyTokens.Scrim -> scrim
        ColorSchemeKeyTokens.Secondary -> secondary
        ColorSchemeKeyTokens.SecondaryContainer -> secondaryContainer
        ColorSchemeKeyTokens.Surface -> surface
        ColorSchemeKeyTokens.SurfaceVariant -> surfaceVariant
        ColorSchemeKeyTokens.Tertiary -> tertiary
        ColorSchemeKeyTokens.TertiaryContainer -> tertiaryContainer
    }
}

object CustomSliderDefaults {
    @Composable
    fun colors(
        thumbColor: Color = SliderTokens.HandleColor.toColor(),
        activeTrackColor: Color = SliderTokens.ActiveTrackColor.toColor(),
        activeTickColor: Color = SliderTokens.TickMarksActiveContainerColor
            .toColor()
            .copy(alpha = SliderTokens.TickMarksActiveContainerOpacity),
        inactiveTrackColor: Color = SliderTokens.InactiveTrackColor.toColor(),
        inactiveTickColor: Color = SliderTokens.TickMarksInactiveContainerColor.toColor()
            .copy(alpha = SliderTokens.TickMarksInactiveContainerOpacity),
        disabledThumbColor: Color = SliderTokens.DisabledHandleColor
            .toColor()
            .copy(alpha = SliderTokens.DisabledHandleOpacity)
            .compositeOver(MaterialTheme.colorScheme.surface),
        disabledActiveTrackColor: Color =
            SliderTokens.DisabledActiveTrackColor
                .toColor()
                .copy(alpha = SliderTokens.DisabledActiveTrackOpacity),
        disabledActiveTickColor: Color = SliderTokens.TickMarksDisabledContainerColor
            .toColor()
            .copy(alpha = SliderTokens.TickMarksDisabledContainerOpacity),
        disabledInactiveTrackColor: Color =
            SliderTokens.DisabledInactiveTrackColor
                .toColor()
                .copy(alpha = SliderTokens.DisabledInactiveTrackOpacity),

        disabledInactiveTickColor: Color = SliderTokens.TickMarksDisabledContainerColor.toColor()
            .copy(alpha = SliderTokens.TickMarksDisabledContainerOpacity),
    ): SliderColors = SliderColors(
        thumbColor = thumbColor,
        activeTrackColor = activeTrackColor,
        activeTickColor = activeTickColor,
        inactiveTrackColor = inactiveTrackColor,
        inactiveTickColor = inactiveTickColor,
        disabledThumbColor = disabledThumbColor,
        disabledActiveTrackColor = disabledActiveTrackColor,
        disabledActiveTickColor = disabledActiveTickColor,
        disabledInactiveTrackColor = disabledInactiveTrackColor,
        disabledInactiveTickColor = disabledInactiveTickColor
    )

    /**
     * Composable function that represents the thumb of the slider.
     *
     * @param thumbValue The value to display on the thumb.
     * @param modifier The modifier for styling the thumb.
     * @param colors The color of the thumb.
     * @param thumbSize The size of the thumb.
     * @param shape The shape of the thumb.
     */
    @Composable
    fun Thumb(
        thumbValue: String,
        interactionSource: MutableInteractionSource,
        modifier: Modifier = Modifier,
        colors: SliderColors = colors(),
        enabled: Boolean = true,
        thumbSize: Dp = ThumbSize,
        shape: Shape = CircleShape,
        content: @Composable () -> Unit = {
            Text(
                text = thumbValue,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        },
    ) {
        val interactions = remember { mutableStateListOf<Interaction>() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> interactions.add(interaction)
                    is PressInteraction.Release -> interactions.remove(interaction.press)
                    is PressInteraction.Cancel -> interactions.remove(interaction.press)
                    is DragInteraction.Start -> interactions.add(interaction)
                    is DragInteraction.Stop -> interactions.remove(interaction.start)
                    is DragInteraction.Cancel -> interactions.remove(interaction.start)
                }
            }
        }
        val elevation = if (interactions.isNotEmpty()) {
            ThumbPressedElevation
        } else {
            ThumbDefaultElevation
        }
        Box(
            modifier = modifier
                .size(thumbSize)
                .indication(
                    interactionSource = interactionSource,
                    indication = ripple(
                        bounded = false,
                        radius = SliderTokens.StateLayerSize / 2
                    )
                )
                .hoverable(interactionSource = interactionSource)
                .shadow(if (enabled) elevation else 0.dp, shape, clip = false)
                .background(colors.thumbColor(enabled).value, shape)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }

    /**
     * Composable function that represents the track of the slider.
     *
     * @param sliderState The positions of the slider.
     * @param modifier The modifier for styling the track.
     * @param colors The color of the track.
     */

    @Composable
    fun Track(
        sliderState: SliderState,
        modifier: Modifier = Modifier,
        colors: SliderColors = colors(),
        enabled: Boolean = true,
    ) {
        SliderDefaults.Track(
            sliderState = sliderState,
            modifier = modifier,
            colors = colors.toOriginal(),
            enabled = enabled
        )
    }

    /**
     * Composable function that represents the indicator of the slider.
     *
     * @param indicatorValue The value to display as the indicator.
     * @param modifier The modifier for styling the indicator.
     * @param style The style of the indicator text.
     */
    @Composable
    fun Indicator(
        indicatorValue: String,
        modifier: Modifier = Modifier,
        style: TextStyle = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal),
    ) {
        Box(modifier = modifier) {
            Text(
                text = indicatorValue,
                style = style,
                textAlign = TextAlign.Center
            )
        }
    }

    /**
     * Composable function that represents the label of the slider.
     *
     * @param labelValue The value to display as the label.
     * @param modifier The modifier for styling the label.
     * @param style The style of the label text.
     */
    @Composable
    fun Label(
        labelValue: String,
        modifier: Modifier = Modifier,
        style: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
    ) {
        Box(modifier = modifier) {
            Text(
                text = labelValue,
                style = style,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun Modifier.track(
    height: Dp = TrackHeight,
    shape: Shape = CircleShape,
) = fillMaxWidth()
    .heightIn(min = height)
    .clip(shape)

fun Modifier.progress(
    sliderState: SliderState,
    height: Dp = TrackHeight,
    shape: Shape = CircleShape,
) =
    fillMaxWidth(fraction = sliderState.valueRange.endInclusive - sliderState.valueRange.start)
        .heightIn(min = height)
        .clip(shape)

private val TrackHeight = 8.dp
private val ThumbSize = 30.dp

private val ThumbDefaultElevation = 1.dp
private val ThumbPressedElevation = 6.dp

@Immutable
class SliderColors internal constructor(
    val thumbColor: Color,
    val activeTrackColor: Color,
    val activeTickColor: Color,
    val inactiveTrackColor: Color,
    val inactiveTickColor: Color,
    val disabledThumbColor: Color,
    val disabledActiveTrackColor: Color,
    val disabledActiveTickColor: Color,
    val disabledInactiveTrackColor: Color,
    val disabledInactiveTickColor: Color,
) {

    @Composable
    internal fun toOriginal() = SliderDefaults.colors(
        thumbColor = thumbColor,
        activeTrackColor = activeTrackColor,
        activeTickColor = activeTickColor,
        inactiveTrackColor = inactiveTrackColor,
        inactiveTickColor = inactiveTickColor,
        disabledThumbColor = disabledThumbColor,
        disabledActiveTrackColor = disabledActiveTrackColor,
        disabledActiveTickColor = disabledActiveTickColor,
        disabledInactiveTrackColor = disabledInactiveTrackColor,
        disabledInactiveTickColor = disabledInactiveTickColor,
    )

    @Composable
    internal fun thumbColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) thumbColor else disabledThumbColor)
    }

    @Composable
    internal fun trackColor(enabled: Boolean, active: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) {
                if (active) activeTrackColor else inactiveTrackColor
            } else {
                if (active) disabledActiveTrackColor else disabledInactiveTrackColor
            }
        )
    }

    @Composable
    internal fun tickColor(enabled: Boolean, active: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) {
                if (active) activeTickColor else inactiveTickColor
            } else {
                if (active) disabledActiveTickColor else disabledInactiveTickColor
            }
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is SliderColors) return false

        if (thumbColor != other.thumbColor) return false
        if (activeTrackColor != other.activeTrackColor) return false
        if (activeTickColor != other.activeTickColor) return false
        if (inactiveTrackColor != other.inactiveTrackColor) return false
        if (inactiveTickColor != other.inactiveTickColor) return false
        if (disabledThumbColor != other.disabledThumbColor) return false
        if (disabledActiveTrackColor != other.disabledActiveTrackColor) return false
        if (disabledActiveTickColor != other.disabledActiveTickColor) return false
        if (disabledInactiveTrackColor != other.disabledInactiveTrackColor) return false
        return disabledInactiveTickColor == other.disabledInactiveTickColor
    }

    override fun hashCode(): Int {
        var result = thumbColor.hashCode()
        result = 31 * result + activeTrackColor.hashCode()
        result = 31 * result + activeTickColor.hashCode()
        result = 31 * result + inactiveTrackColor.hashCode()
        result = 31 * result + inactiveTickColor.hashCode()
        result = 31 * result + disabledThumbColor.hashCode()
        result = 31 * result + disabledActiveTrackColor.hashCode()
        result = 31 * result + disabledActiveTickColor.hashCode()
        result = 31 * result + disabledInactiveTrackColor.hashCode()
        result = 31 * result + disabledInactiveTickColor.hashCode()
        return result
    }
}
