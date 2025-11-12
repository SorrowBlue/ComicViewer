package com.sorrowblue.comicviewer.feature.collection.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.feature.collection.section.CollectionList
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.PrimaryActionButtonMenu
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_label_collection
import comicviewer.feature.collection.generated.resources.collection_label_smart_collection
import comicviewer.feature.collection.generated.resources.collection_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AdaptiveNavigationSuiteScaffoldState.CollectionListScreen(
    lazyPagingItems: LazyPagingItems<Collection>,
    lazyListState: LazyListState,
    onItemClick: (Collection) -> Unit,
    onEditClick: (Collection) -> Unit,
    onDeleteClick: (Collection) -> Unit,
    onSettingsClick: () -> Unit,
    onCreateBasicCollectionClick: () -> Unit,
    onCreateSmartCollectionClick: () -> Unit,
) {
    AdaptiveNavigationSuiteScaffold {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(Res.string.collection_title))
                    },
                    actions = {
                        SettingsIconButton(onClick = onSettingsClick)
                    },
                )
            },
            floatingActionButton = {
                PrimaryActionButtonMenu {
                    FloatingActionButtonMenuItem(
                        onClick = {
                            floatingActionButtonState.toggleMenu(false)
                            onCreateBasicCollectionClick()
                        },
                        text = {
                            Text(stringResource(Res.string.collection_label_collection))
                        },
                        icon = {
                            Icon(
                                imageVector = ComicIcons.Favorite,
                                contentDescription = null,
                            )
                        },
                    )
                    FloatingActionButtonMenuItem(
                        onClick = {
                            floatingActionButtonState.toggleMenu(false)
                            onCreateSmartCollectionClick()
                        },
                        text = {
                            Text(stringResource(Res.string.collection_label_smart_collection))
                        },
                        icon = {
                            Icon(
                                imageVector = ComicIcons.CollectionsBookmark,
                                contentDescription = null,
                            )
                        },
                    )
                }
            },
        ) { contentPadding ->
            CollectionList(
                lazyPagingItems = lazyPagingItems,
                onItemClick = onItemClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                lazyListState = lazyListState,
                contentPadding = contentPadding,
            )
        }
    }
}
