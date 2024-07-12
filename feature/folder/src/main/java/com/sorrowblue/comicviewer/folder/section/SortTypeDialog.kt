package com.sorrowblue.comicviewer.folder.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.folder.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Destination<ExternalModuleGraph>(style = DestinationStyle.Dialog::class)
@Composable
internal fun SortTypeDialog(fileSort: SortType, resultNavigator: ResultBackNavigator<SortType>) {
    SortTypeDialog(
        currentSortType = fileSort,
        onDismissRequest = resultNavigator::navigateBack,
        onClick = resultNavigator::navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortTypeDialog(
    currentSortType: SortType,
    onDismissRequest: () -> Unit,
    onClick: (SortType) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        contentWindowInsets = { WindowInsets(0) }
    ) {
        Text(
            text = stringResource(R.string.folder_sorttype_title_sort_by),
            style = ComicTheme.typography.titleLarge,
            modifier = Modifier
                .padding(horizontal = ComicTheme.dimension.margin)
                .padding(bottom = ComicTheme.dimension.padding)
        )
        HorizontalDivider()
        LazyColumn {
            items(SortType.entries) { item ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = item.displayText()),
                        )
                    },
                    trailingContent = {
                        if (item == currentSortType) {
                            Icon(ComicIcons.Check, contentDescription = null)
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable { onClick(item) }
                )
            }
        }
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

private fun SortType.displayText() = when (this) {
    is SortType.Date -> if (isAsc) R.string.folder_sorttype_label_date_asc else R.string.folder_sorttype_label_date_desc
    is SortType.Name -> if (isAsc) R.string.folder_sorttype_label_name_asc else R.string.folder_sorttype_label_name_desc
    is SortType.Size -> if (isAsc) R.string.folder_sorttype_label_size_asc else R.string.folder_sorttype_label_size_desc
}
