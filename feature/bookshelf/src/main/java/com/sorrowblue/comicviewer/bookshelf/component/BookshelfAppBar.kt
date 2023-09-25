package com.sorrowblue.comicviewer.bookshelf.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfAppBar(onSettingsClick: () -> Unit, scrollBehavior: TopAppBarScrollBehavior) {

    TopAppBar(
        title = { Text(stringResource(id = R.string.bookshelf_list_title)) },
        actions = {
            PlainTooltipBox(tooltip = { Text("Open Settings") }) {
                IconButton(onSettingsClick, Modifier.tooltipAnchor()) {
                    Icon(ComicIcons.Settings, "Open Settings")
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}
