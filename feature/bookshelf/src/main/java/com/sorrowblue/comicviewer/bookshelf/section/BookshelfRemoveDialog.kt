package com.sorrowblue.comicviewer.bookshelf.section

import android.os.Parcelable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfGraph
import com.sorrowblue.comicviewer.feature.bookshelf.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookshelfRemoveDialogArgs(
    val title: String,
) : Parcelable

@Destination<BookshelfGraph>(
    navArgs = BookshelfRemoveDialogArgs::class,
    style = DestinationStyle.Dialog::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun BookshelfRemoveDialog(
    args: BookshelfRemoveDialogArgs,
    destinationsNavigator: ResultBackNavigator<Boolean>,
) {
    AlertDialog(
        onDismissRequest = { destinationsNavigator.navigateBack() },
        title = {
            Text(text = stringResource(id = R.string.bookshelf_edit_title_remove))
        },
        text = {
            Text(text = stringResource(id = R.string.bookshelf_remove_label, args.title))
        },
        confirmButton = {
            TextButton(onClick = { destinationsNavigator.navigateBack(true) }) {
                Text(stringResource(id = R.string.bookshelf_remove_label_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = { destinationsNavigator.navigateBack(false) }) {
                Text(stringResource(id = android.R.string.cancel))
            }
        }
    )
}
