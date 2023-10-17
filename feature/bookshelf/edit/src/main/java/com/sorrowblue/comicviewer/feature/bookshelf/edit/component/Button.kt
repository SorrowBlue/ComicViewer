package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbEditScreenUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Composable
internal fun SaveButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        enabled = enabled
    ) {
        Icon(
            modifier = Modifier.size(ButtonDefaults.IconSize),
            imageVector = ComicIcons.Save,
            contentDescription = null
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = stringResource(id = R.string.bookshelf_edit_label_save))
    }
}

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
