package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.entity.settings.BindingDirection
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.material3.ListItemRadioButton
import com.sorrowblue.comicviewer.framework.resource.R

internal data class DirectionSheetUiState(
    val direction: BindingDirection = BindingDirection.RTL
)

@Composable
internal fun DirectionSheet(
    uiState: DirectionSheetUiState = DirectionSheetUiState(),
    onBindingDirectionChange: (BindingDirection) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppMaterialTheme.dimens.margin),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_undraw_book_lover_re_rwjy),
            contentDescription = null
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

@Preview
@Composable
private fun PreviewDirectionSheet() {
    AppMaterialTheme {
        Surface {
            DirectionSheet()
        }
    }
}
