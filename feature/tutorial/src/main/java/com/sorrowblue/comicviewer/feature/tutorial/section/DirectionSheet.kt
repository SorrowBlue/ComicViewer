package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.entity.settings.BindingDirection
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawBookLover
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.ComicPreviews
import com.sorrowblue.comicviewer.framework.ui.material3.ListItemRadioButton

internal data class DirectionSheetUiState(
    val direction: BindingDirection = BindingDirection.RTL,
)

@Composable
internal fun DirectionSheet(
    uiState: DirectionSheetUiState = DirectionSheetUiState(),
    onBindingDirectionChange: (BindingDirection) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(ComicTheme.dimension.margin),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            imageVector = ComicIcons.UndrawBookLover,
            contentDescription = null,
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth(0.5f),

            )
        Spacer(modifier = Modifier.size(16.dp))

        Text(text = "Reading direction", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.size(8.dp))

        ListItemRadioButton(
            headlineContent = { Text(text = "Right to left") },
            selected = uiState.direction == BindingDirection.RTL,
            onCheckedChange = {
                if (it) {
                    onBindingDirectionChange(BindingDirection.RTL)
                }
            }
        )
        ListItemRadioButton(
            headlineContent = { Text(text = "Left to Right") },
            selected = uiState.direction == BindingDirection.LTR,
            onCheckedChange = {
                if (it) {
                    onBindingDirectionChange(BindingDirection.LTR)
                }
            }
        )

        Text(
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec sit amet finibus elit. Vivamus scelerisque, risus eu accumsan interdum, nunc quam aliquet lectus, vel aliquam ipsum lectus eu urna. Morbi quis elementum turpis, sed ultrices sem. Nullam vel viverra libero. Cras in porttitor erat. Nunc eget velit a justo sagittis blandit lobortis a mi.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@ComicPreviews
@Composable
private fun PreviewDirectionSheet() {
    ComicTheme {
        Surface {
            DirectionSheet()
        }
    }
}
