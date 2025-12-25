package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FileAttributeChips(fileAttribute: FileAttribute, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "属性",
            style = ComicTheme.typography.labelSmall,
            modifier = Modifier,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.minPadding),
        ) {
            fileAttribute.let {
                if (it.archive) {
                    AssistChip(onClick = {}, label = { Text(text = "アーカイブ") })
                }
                if (it.compressed) {
                    AssistChip(onClick = {}, label = { Text(text = "圧縮") })
                }
                if (it.hidden) {
                    AssistChip(onClick = {}, label = { Text(text = "隠しファイル") })
                }
                if (it.normal) {
                    AssistChip(onClick = {}, label = { Text(text = "標準") })
                }
                if (it.directory) {
                    AssistChip(onClick = {}, label = { Text(text = "ディレクトリ") })
                }
                if (it.readonly) {
                    AssistChip(onClick = {}, label = { Text(text = "読取専用") })
                }
                if (it.sharedRead) {
                    AssistChip(onClick = {}, label = { Text(text = "読取共有アクセス") })
                }
                if (it.system) {
                    AssistChip(onClick = {}, label = { Text(text = "システム") })
                }
                if (it.temporary) {
                    AssistChip(onClick = {}, label = { Text(text = "一時ファイル") })
                }
                if (it.volume) {
                    AssistChip(onClick = {}, label = { Text(text = "ボリューム") })
                }
            }
        }
    }
}
