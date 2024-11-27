package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.bookshelf.info.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Composable
internal fun BookshelfInfoActionButtons(
    enabled: Boolean,
    onRemoveClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        TextButton(
            onClick = onRemoveClick,
            enabled = enabled,
            contentPadding = ButtonDefaults.TextButtonWithIconContentPadding
        ) {
            Icon(imageVector = ComicIcons.Delete, contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.bookshelf_info_btn_remove))
        }
        FilledTonalButton(
            onClick = onEditClick,
            enabled = enabled,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        ) {
            Icon(imageVector = ComicIcons.Edit, contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.bookshelf_info_btn_edit))
        }
    }
}
