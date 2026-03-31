package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun ActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val colors = CardDefaults.cardColors()
    FilledTonalButton(
        enabled = enabled,
        onClick = onClick,
        shapes = ButtonDefaults.shapes(
            ButtonDefaults.mediumPressedShape,
        ),
        modifier = modifier.height(ButtonDefaults.MediumContainerHeight)
            .rotatingBorder(
                shape = ButtonDefaults.outlinedShape,
                isVisible = loading,
                borderWidth = 4.dp,
                borderColor = ComicTheme.colorScheme.primary,
                segmentLengthRatio = 0.4f,
                durationMillis = 2000,
                strokeCap = StrokeCap.Round,
            ),
    ) {
        Icon(
            icon,
            null,
            tint = ComicTheme.colorScheme.primary,
            modifier = Modifier.size(ButtonDefaults.MediumIconSize),
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        ProvideTextStyle(ComicTheme.typography.labelLarge.copy(color = colors.contentColor)) {
            text()
        }
    }
}
