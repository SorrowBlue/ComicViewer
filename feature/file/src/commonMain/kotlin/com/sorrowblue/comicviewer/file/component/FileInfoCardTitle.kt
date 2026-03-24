package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun FileInfoCardTitle(
    icon: ImageVector,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Icon(
            icon,
            null,
            tint = ComicTheme.colorScheme.primary,
            modifier = Modifier.size(ButtonDefaults.MediumIconSize),
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        ProvideTextStyle(
            ComicTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = ComicTheme.colorScheme.primary,
            ),
        ) {
            title()
        }
    }
}
