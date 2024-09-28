package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.file.ReadLaterUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun ReadlaterButton(uiState: ReadLaterUiState, onClick: () -> Unit) {
    if (uiState.checked) {
        FilledTonalButton(
            onClick = onClick,
            enabled = !uiState.loading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ComicTheme.dimension.minPadding * 4)
                .padding(horizontal = ComicTheme.dimension.minPadding * 4)
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Icon(imageVector = ComicIcons.WatchLater, contentDescription = null)
            }
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.file_info_label_add_read_later))
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = !uiState.loading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ComicTheme.dimension.minPadding * 4)
                .padding(horizontal = ComicTheme.dimension.minPadding * 4)
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Icon(imageVector = ComicIcons.WatchLater, contentDescription = null)
            }
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.file_info_label_add_read_later))
        }
    }
}
