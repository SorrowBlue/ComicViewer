package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenUiState
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AuthButtons(
    currentAuth: SmbEditScreenUiState.Auth,
    onAuthChange: (SmbEditScreenUiState.Auth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val list = remember { SmbEditScreenUiState.Auth.entries.toPersistentList() }
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        list.forEachIndexed { index, auth ->
            SegmentedButton(
                selected = auth == currentAuth,
                onClick = { onAuthChange(auth) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = list.size),
                icon = {
                    if (auth == currentAuth) {
                        SegmentedButtonDefaults.ActiveIcon()
                    }
                }
            ) {
                Text(
                    text = stringResource(
                        when (auth) {
                            SmbEditScreenUiState.Auth.Guest -> R.string.bookshelf_edit_label_guest
                            SmbEditScreenUiState.Auth.UserPass -> R.string.bookshelf_edit_label_username_password
                        }
                    )
                )
            }
        }
    }
}
