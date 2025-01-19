package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.framework.annotation.Destination
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.navigation.DestinationStyle
import com.sorrowblue.comicviewer.framework.navigation.NavResultSender
import comicviewer.feature.folder.generated.resources.Res
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_date_asc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_date_desc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_name_asc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_name_desc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_size_asc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_size_desc
import comicviewer.feature.folder.generated.resources.folder_sorttype_title_sort_by
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class SortTypeSelect(val sortType: SortType)

@Destination<SortTypeSelect>(style = DestinationStyle.Dialog::class)
@Composable
internal fun SortTypeSelectScreen(
    route: SortTypeSelect,
    resultNavigator: NavResultSender<SortType>,
) {
    SortTypeSelectScreen(
        currentSortType = route.sortType,
        onDismissRequest = resultNavigator::navigateBack,
        onClick = resultNavigator::navigateBack
    )
}

@Composable
private fun SortTypeSelectScreen(
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
            text = stringResource(Res.string.folder_sorttype_title_sort_by),
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
                            text = stringResource(item.displayText()),
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
    is SortType.Date -> if (isAsc) Res.string.folder_sorttype_label_date_asc else Res.string.folder_sorttype_label_date_desc
    is SortType.Name -> if (isAsc) Res.string.folder_sorttype_label_name_asc else Res.string.folder_sorttype_label_name_desc
    is SortType.Size -> if (isAsc) Res.string.folder_sorttype_label_size_asc else Res.string.folder_sorttype_label_size_desc
}
