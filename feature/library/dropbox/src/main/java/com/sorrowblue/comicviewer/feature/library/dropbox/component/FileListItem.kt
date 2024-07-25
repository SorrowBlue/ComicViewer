package com.sorrowblue.comicviewer.feature.library.dropbox.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Composable
internal fun FileListItem(file: File, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = { Text(text = file.name) },
        trailingContent = {
            Text(text = file.size.toString())
        },
        leadingContent = {
            when (file) {
                is Book -> Icon(imageVector = ComicIcons.InsertDriveFile, contentDescription = null)
                is Folder -> Icon(imageVector = ComicIcons.Folder, contentDescription = null)
            }
        },
        modifier = modifier.clickable(onClick = onClick)
    )
}
