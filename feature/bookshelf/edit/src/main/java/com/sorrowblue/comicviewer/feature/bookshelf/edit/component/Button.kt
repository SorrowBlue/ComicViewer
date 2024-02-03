package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenUiState

@Composable
internal fun AuthButtons(
    currentAuth: SmbEditScreenUiState.Auth,
    onAuthChange: (SmbEditScreenUiState.Auth) -> Unit,
) {
    MaterialButtons(
        size = remember { SmbEditScreenUiState.Auth.entries.size },
        label = {
            Text(
                stringResource(
                    when (SmbEditScreenUiState.Auth.entries[it]) {
                        SmbEditScreenUiState.Auth.Guest -> R.string.bookshelf_edit_label_guest
                        SmbEditScreenUiState.Auth.UserPass -> R.string.bookshelf_edit_label_username_password
                    }
                )
            )
        },
        selectedIndex = currentAuth.ordinal,
        onChange = { onAuthChange(SmbEditScreenUiState.Auth.entries[it]) },
    )
}
