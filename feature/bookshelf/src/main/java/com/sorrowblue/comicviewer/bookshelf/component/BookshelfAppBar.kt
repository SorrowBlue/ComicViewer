package com.sorrowblue.comicviewer.bookshelf.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.MobilePreviews
import com.sorrowblue.comicviewer.framework.ui.material3.AppBarAction
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveTopAppBar
import kotlinx.collections.immutable.toPersistentList

enum class BookshelfAppBarAction(override val icon: ImageVector, override val label: String) :
    AppBarAction {
    Settings(ComicIcons.Settings, "Settings"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfAppBar(onSettingsClick: () -> Unit, scrollBehavior: TopAppBarScrollBehavior) {
    ResponsiveTopAppBar(
        title = { Text(text = stringResource(id = R.string.bookshelf_list_title)) },
        actions = remember { BookshelfAppBarAction.entries.toPersistentList() },
        onClick = {
            when (it) {
                BookshelfAppBarAction.Settings -> onSettingsClick()
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@MobilePreviews
@Composable
private fun PreviewBookshelfAppBar() {
    PreviewTheme {
        BookshelfAppBar({}, TopAppBarDefaults.pinnedScrollBehavior())
    }
}
