package com.sorrowblue.comicviewer.feature.settings.viewer

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
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_desc_binding_direction
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_binding_direction_ltr
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_binding_direction_rtl
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_title_binding_direction
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BindingDirectionScreen(
    bindingDirection: BindingDirection,
    onClick: (BindingDirection) -> Unit,
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(stringResource(Res.string.settings_viewer_title_binding_direction))
        },
    ) { contentPadding ->
        Column {
            Text(
                text = stringResource(Res.string.settings_viewer_desc_binding_direction),
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
                    onClick(BindingDirection.RTL)
                },
            )
            ListItemRadioButton(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = {
                    Text(BindingDirection.LTR.displayName)
                },
                selected = bindingDirection == BindingDirection.LTR,
                onCheckedChange = {
                    onClick(BindingDirection.LTR)
                },
            )
        }
    }
}

internal val BindingDirection.displayName: String
    @Composable
    get() = when (this) {
        BindingDirection.LTR -> stringResource(
            Res.string.settings_viewer_label_binding_direction_ltr,
        )

        BindingDirection.RTL -> stringResource(
            Res.string.settings_viewer_label_binding_direction_rtl,
        )
    }

@Preview
@Composable
private fun BindingDirectionScreenPreview() {
    PreviewTheme {
        BindingDirectionScreen(
            bindingDirection = BindingDirection.RTL,
            onClick = {},
        )
    }
}
