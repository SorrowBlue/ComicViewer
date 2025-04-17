package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_btn_delete
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_btn_edit
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RowScope.BottomActions(
    onRemoveClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onRemoveClick,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        Icon(imageVector = ComicIcons.Delete, contentDescription = null)
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = stringResource(Res.string.bookshelf_info_btn_delete))
    }
    FilledTonalButton(
        onClick = onEditClick,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
    ) {
        Icon(imageVector = ComicIcons.Edit, contentDescription = null)
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = stringResource(Res.string.bookshelf_info_btn_edit))
    }
}
