package com.sorrowblue.comicviewer.folder.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.settings.SortType
import com.sorrowblue.comicviewer.feature.folder.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.PreviewComic
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import kotlinx.collections.immutable.toPersistentList

enum class Sort(val labelRes: Int) {
    NAME_ASC(R.string.folder_label_name_asc),
    NAME_DESC(R.string.folder_label_name_desc),
    SIZE_DESC(R.string.folder_label_size_asc),
    SIZE_ASC(R.string.folder_label_size_desc),
    DATE_ASC(R.string.folder_label_date_asc),
    DATE_DESC(R.string.folder_label_date_desc),
    ;

    companion object {
        fun from(sortType: SortType): Sort {
            return when (sortType) {
                is SortType.DATE -> if (sortType.isAsc) DATE_ASC else DATE_DESC
                is SortType.NAME -> if (sortType.isAsc) NAME_ASC else NAME_DESC
                is SortType.SIZE -> if (sortType.isAsc) SIZE_ASC else SIZE_DESC
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortSheet(
    currentSort: Sort,
    onDismissRequest: () -> Unit,
    onClick: (Sort) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sortList = remember { Sort.entries.toPersistentList() }
    ModalBottomSheet(onDismissRequest = onDismissRequest, modifier = modifier) {
        LazyColumn {
            items(sortList, key = Sort::labelRes) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onClick(it)
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = it.labelRes),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f, true)
                    )
                    if (it == currentSort) {
                        Icon(ComicIcons.Check, contentDescription = "Selected")
                    }
                }
            }
        }
    }
}

@PreviewComic
@Composable
private fun PreviewSortSheet() {
    PreviewTheme {
        SortSheet(
            currentSort = Sort.NAME_ASC,
            onDismissRequest = {},
            onClick = {}
        )
    }
}
