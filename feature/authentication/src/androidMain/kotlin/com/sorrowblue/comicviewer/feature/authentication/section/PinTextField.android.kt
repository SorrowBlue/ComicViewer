package com.sorrowblue.comicviewer.feature.authentication.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Preview
@Composable
private fun PreviewPinTextField() {
    PreviewTheme {
        Column {
            var pin by remember { mutableStateOf("1111") }
            PinTextField(
                pin = pin,
                onPinChange = { pin = it },
                onNextClick = { pin = "" },
                modifier = Modifier
                    .width(48.dp * 5)
                    .height(48.dp)
            )
        }
    }
}
