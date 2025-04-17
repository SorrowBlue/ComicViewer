package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton

@Composable
internal fun BookshelfEditLoadingScreen(
    isDialog: Boolean,
    uiState: BookshelfEditScreenUiState.Loading,
    onBackClick: () -> Unit,
) {
    if (isDialog) {
        AlertDialog(onDismissRequest = onBackClick, confirmButton = {}, title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = uiState.editMode.title)
                Spacer(modifier = Modifier.weight(1f))
                CloseIconButton(onClick = onBackClick)
            }
        }, text = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        })
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = uiState.editMode.title) },
                    navigationIcon = { CloseIconButton(onClick = onBackClick) },
                    windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
