package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.file.asDateTime
import com.sorrowblue.comicviewer.file.asFileSize

@Composable
internal fun FileInfoList(file: File, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        val transparentColor = ListItemDefaults.colors(containerColor = Color.Transparent)
        val clipboardManager = LocalClipboardManager.current
        ListItem(
            overlineContent = { Text(text = "パス") },
            headlineContent = { Text(text = file.path) },
            colors = transparentColor,
            modifier = Modifier.combinedClickable(onLongClick = {
                clipboardManager.setText(AnnotatedString(file.path))
            }, onClick = {})
        )
        ListItem(
            overlineContent = { Text(text = "種類") },
            headlineContent = { Text(text = if (file is IFolder) "フォルダ" else file.name.extension) },
            colors = transparentColor
        )
        ListItem(
            overlineContent = { Text(text = "サイズ") },
            headlineContent = { Text(text = file.size.asFileSize) },
            colors = transparentColor
        )
        ListItem(
            overlineContent = { Text(text = stringResource(R.string.file_label_modified_date)) },
            headlineContent = { Text(text = file.lastModifier.asDateTime) },
            colors = transparentColor
        )
        if (file is Book) {
            ListItem(
                overlineContent = { Text(text = "ページ数") },
                headlineContent = {
                    Text(
                        text = pluralStringResource(
                            id = R.plurals.file_text_page_count,
                            count = file.totalPageCount,
                            file.totalPageCount
                        )
                    )
                },
                colors = transparentColor
            )
            ListItem(
                overlineContent = { Text(text = "最後に読んだ日時") },
                headlineContent = { Text(text = file.lastReadTime.asDateTime) },
                colors = transparentColor
            )
        }
    }
}
