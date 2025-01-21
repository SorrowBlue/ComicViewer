package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.feature.tutorial.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawBookLover
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.ListItemRadioButton
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.tutorial.generated.resources.Res
import comicviewer.feature.tutorial.generated.resources.tutorial_label_direction_ltr
import comicviewer.feature.tutorial.generated.resources.tutorial_label_direction_rtl
import comicviewer.feature.tutorial.generated.resources.tutorial_text_direction
import org.jetbrains.compose.resources.stringResource

internal data class DirectionSheetUiState(
    val direction: BindingDirection = BindingDirection.RTL,
)

@Composable
internal fun DirectionSheet(
    uiState: DirectionSheetUiState,
    onBindingDirectionChange: (BindingDirection) -> Unit,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(ComicTheme.dimension.margin),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = ComicIcons.UndrawBookLover,
            contentDescription = null,
            modifier = Modifier
                .sizeIn(maxHeight = 400.dp, maxWidth = 400.dp)
                .fillMaxSize(0.5f),
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = stringResource(Res.string.tutorial_text_direction),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.size(16.dp))

        ListItemRadioButton(
            headlineContent = { Text(text = stringResource(Res.string.tutorial_label_direction_rtl)) },
            selected = uiState.direction == BindingDirection.RTL,
            onCheckedChange = {
                if (it) {
                    onBindingDirectionChange(BindingDirection.RTL)
                }
            },
            modifier = Modifier
                .heightIn(max = 48.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        )
        ListItemRadioButton(
            headlineContent = { Text(text = stringResource(Res.string.tutorial_label_direction_ltr)) },
            selected = uiState.direction == BindingDirection.LTR,
            onCheckedChange = {
                if (it) {
                    onBindingDirectionChange(BindingDirection.LTR)
                }
            },
            modifier = Modifier
                .heightIn(max = 48.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        )
    }
}
