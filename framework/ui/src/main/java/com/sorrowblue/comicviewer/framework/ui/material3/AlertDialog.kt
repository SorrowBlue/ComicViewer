package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column {
                if (title != null) {
                    Box(
                        modifier = Modifier.padding(
                            start = 24.dp,
                            top = 24.dp,
                            end = 24.dp,
                            bottom = 16.dp
                        )
                    ) {
                        CompositionLocalProvider(LocalTextStyle provides ComicTheme.typography.headlineSmall) {
                            title()
                        }
                    }
                }
                content(PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp))
            }
        }
    }
}
