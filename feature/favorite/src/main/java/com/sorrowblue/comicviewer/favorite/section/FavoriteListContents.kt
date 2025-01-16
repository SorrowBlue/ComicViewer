package com.sorrowblue.comicviewer.favorite.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteListCardItem
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteListItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawNoData
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.adaptive.ResponsiveLazyColumn
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData

internal sealed interface FavoriteListContentsAction {
    data class FavoriteClick(val favoriteId: FavoriteId) : FavoriteListContentsAction
    data class EditClick(val favoriteId: FavoriteId) : FavoriteListContentsAction
    data class DeleteClick(val favoriteId: FavoriteId) : FavoriteListContentsAction
}

@Composable
internal fun FavoriteListContents(
    lazyPagingItems: LazyPagingItems<Favorite>,
    onAction: (FavoriteListContentsAction) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContent(
            imageVector = ComicIcons.UndrawNoData,
            text = stringResource(id = R.string.favorite_list_label_no_favorites),
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
    } else {
        val isCompact = isCompactWindowClass()
        ResponsiveLazyColumn(
            state = lazyListState,
            contentPadding = contentPadding + PaddingValues(bottom = 88.dp),
            modifier = modifier
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id.value },
                contentType = { isCompact }
            ) { index ->
                lazyPagingItems[index]?.let {
                    val content = remember {
                        movableContentOf {
                            var expanded by remember { mutableStateOf(false) }
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(ComicIcons.MoreVert, null)
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit") },
                                    onClick = {
                                        expanded = false
                                        onAction(FavoriteListContentsAction.EditClick(it.id))
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        expanded = false
                                        onAction(FavoriteListContentsAction.DeleteClick(it.id))
                                    }
                                )
                            }
                        }
                    }
                    if (isCompact) {
                        FavoriteListItem(
                            favorite = it,
                            onClick = { onAction(FavoriteListContentsAction.FavoriteClick(it.id)) },
                            content = content
                        )
                    } else {
                        FavoriteListCardItem(
                            favorite = it,
                            onClick = { onAction(FavoriteListContentsAction.FavoriteClick(it.id)) },
                            content = content
                        )
                    }
                }
            }
        }
    }
}
