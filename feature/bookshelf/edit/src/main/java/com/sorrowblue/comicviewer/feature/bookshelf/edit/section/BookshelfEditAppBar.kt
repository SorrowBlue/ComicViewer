package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.bookshelf.edit.EditMode
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.responsive.FullScreenTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookshelfEditAppBar(
    editMode: EditMode,
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    FullScreenTopAppBar(
        title = {
            Text(
                text = when (editMode) {
                    EditMode.Register -> stringResource(id = R.string.bookshelf_edit_title_register)
                    EditMode.Change -> stringResource(id = R.string.bookshelf_edit_title_edit)
                }
            )
        },
        navigationIcon = {
            IconButton(onBackClick) {
                Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
            }
        },
        scrollBehavior = scrollBehavior
    )
}
