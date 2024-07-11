package com.sorrowblue.comicviewer.folder.section

import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.folder.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

enum class SortItem(val label: Int) {
    Name(R.string.folder_label_name),
    Size(R.string.folder_label_size),
    Date(R.string.folder_label_date),
}

enum class SortOrder(val label: Int) {
    Asc(R.string.folder_label_asc),
    Desc(R.string.folder_label_desc),
}

interface SortSheetState {

    val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase

    val scope: CoroutineScope

    val sortSheetUiState: SortSheetUiState

    fun openSortSheet()

    fun onSortSheetDismissRequest()

    fun onSortItemClick(sortItem: SortItem) {
        scope.launch {
            manageFolderDisplaySettingsUseCase.edit {
                it.copy(
                    sortType = when (sortItem) {
                        SortItem.Date -> SortType.Date(sortSheetUiState.currentSortOrder == SortOrder.Asc)
                        SortItem.Name -> SortType.Name(sortSheetUiState.currentSortOrder == SortOrder.Asc)
                        SortItem.Size -> SortType.Size(sortSheetUiState.currentSortOrder == SortOrder.Asc)
                    }
                )
            }
        }
    }

    fun onSortOrderClick(sortOrder: SortOrder) {
        scope.launch {
            manageFolderDisplaySettingsUseCase.edit {
                it.copy(
                    sortType = when (sortOrder) {
                        SortOrder.Asc -> it.sortType.copy2(isAsc = true)
                        SortOrder.Desc -> it.sortType.copy2(isAsc = false)
                    }
                )
            }
        }
    }
}

@Parcelize
data class SortSheetUiState(
    val isVisible: Boolean = false,
    val currentSortItem: SortItem = SortItem.Name,
    val currentSortOrder: SortOrder = SortOrder.Asc,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortSheet(
    uiState: SortSheetUiState,
    onDismissRequest: () -> Unit,
    onSortItemClick: (SortItem) -> Unit,
    onSortOrderClick: (SortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!uiState.isVisible) return
    val sortItems = remember { SortItem.entries.toPersistentList() }
    val sortOrders = remember { SortOrder.entries.toPersistentList() }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        contentWindowInsets = { WindowInsets(0) }
    ) {
        LazyColumn {
            items(sortItems.size, key = { sortItems[it] }) {
                val item = sortItems[it]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSortItemClick(item)
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = item.label),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f, true)
                    )
                    if (item == uiState.currentSortItem) {
                        Icon(ComicIcons.Check, contentDescription = null)
                    }
                }
            }
            item("divider") {
                HorizontalDivider()
            }
            items(sortOrders.size, key = { sortOrders[it] }) {
                val item = sortOrders[it]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSortOrderClick(item)
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = item.label),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f, true)
                    )
                    if (item == uiState.currentSortOrder) {
                        Icon(ComicIcons.Check, contentDescription = null)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}
