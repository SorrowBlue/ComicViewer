package com.sorrowblue.comicviewer.feature.settings.viewer.subscreen.readingdirection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import com.sorrowblue.comicviewer.framework.ui.material3.ListItemRadioButton
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.viewer.generated.resources.Res
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_reading_direction_desc
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_reading_direction_label_ltr
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_reading_direction_label_rtl
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_reading_direction_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ReadingDirectionScreen(
    bindingDirection: BindingDirection,
    onBindingDirectionChange: (BindingDirection) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(stringResource(Res.string.settings_viewer_reading_direction_title))
        },
    ) { contentPadding ->
        Column {
            Text(
                text = stringResource(Res.string.settings_viewer_reading_direction_desc),
                modifier = Modifier
                    .padding(contentPadding.only(PaddingValuesSides.Horizontal)),
            )
            Spacer(Modifier.size(16.dp))
            ListItemRadioButton(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = {
                    Text(BindingDirection.RTL.displayName)
                },
                selected = bindingDirection == BindingDirection.RTL,
                onCheckedChange = {
                    onBindingDirectionChange(BindingDirection.RTL)
                },
            )
            ListItemRadioButton(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = {
                    Text(BindingDirection.LTR.displayName)
                },
                selected = bindingDirection == BindingDirection.LTR,
                onCheckedChange = {
                    onBindingDirectionChange(BindingDirection.LTR)
                },
            )
        }
    }
}

internal val BindingDirection.displayName: String
    @Composable
    get() = when (this) {
        BindingDirection.LTR -> stringResource(
            Res.string.settings_viewer_reading_direction_label_ltr,
        )

        BindingDirection.RTL -> stringResource(
            Res.string.settings_viewer_reading_direction_label_rtl,
        )
    }

@Preview
@Composable
private fun ReadingDirectionScreenPreview() {
    PreviewTheme {
        ReadingDirectionScreen(
            bindingDirection = BindingDirection.RTL,
            onBindingDirectionChange = {},
            onDismissRequest = {}
        )
    }
}
