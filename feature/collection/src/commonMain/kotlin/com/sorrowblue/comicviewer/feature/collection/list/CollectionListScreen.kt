package com.sorrowblue.comicviewer.feature.collection.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.feature.collection.section.CollectionListAppBar
import com.sorrowblue.comicviewer.feature.collection.section.CollectionListContents
import com.sorrowblue.comicviewer.feature.collection.section.CollectionListContentsAction
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_label_create
import comicviewer.feature.collection.generated.resources.collection_label_collection
import comicviewer.feature.collection.generated.resources.collection_label_smart_collection
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Serializable
internal data object CollectionList

internal interface CollectionListScreenNavigator {
    fun onSettingsClick()
    fun onCreateSmartCollectionClick()
    fun onCreateBasicCollectionClick()
    fun onCollectionEditClick(collection: Collection)
    fun onCollectionDeleteClick(collection: Collection)
    fun navigateToCollection(id: CollectionId)
}

@Destination<CollectionList>
@Composable
internal fun CollectionListScreen(navigator: CollectionListScreenNavigator = koinInject()) {
    val state = rememberCollectionListScreenState()
    CollectionListScreen(
        lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems(),
        lazyListState = state.lazyListState,
        onContentsAction = {
            when (it) {
                is CollectionListContentsAction.CollectionListClick -> navigator.navigateToCollection(
                    it.id
                )

                is CollectionListContentsAction.DeleteClick -> navigator.onCollectionDeleteClick(it.collection)
                is CollectionListContentsAction.EditClick -> navigator.onCollectionEditClick(it.collection)
            }
        },
        onSettingsClick = navigator::onSettingsClick,
        onCreateSmartCollectionClick = navigator::onCreateSmartCollectionClick,
        onCreateBasicCollectionClick = navigator::onCreateBasicCollectionClick,
    )
}

@Composable
private fun CollectionListScreen(
    lazyPagingItems: LazyPagingItems<Collection>,
    lazyListState: LazyListState,
    onContentsAction: (CollectionListContentsAction) -> Unit,
    onSettingsClick: () -> Unit,
    onCreateBasicCollectionClick: () -> Unit,
    onCreateSmartCollectionClick: () -> Unit,
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            CollectionListAppBar(
                onSettingsClick = onSettingsClick,
                scrollBehavior = appBarScrollBehavior,
                scrollableState = lazyListState
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                var show by remember { mutableStateOf(false) }
                val containerColor by animateColorAsState(
                    if (show) ComicTheme.colorScheme.secondaryContainer else FloatingActionButtonDefaults.containerColor
                )
                AnimatedVisibility(show) {
                    Column(horizontalAlignment = Alignment.End) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                show = !show
                                onCreateBasicCollectionClick()
                            },
                            text = {
                                Text(stringResource(Res.string.collection_label_collection))
                            },
                            icon = {
                                Icon(
                                    imageVector = ComicIcons.Favorite,
                                    contentDescription = null
                                )
                            }
                        )
                        Spacer(Modifier.size(ComicTheme.dimension.padding))
                        ExtendedFloatingActionButton(
                            onClick = {
                                show = !show
                                onCreateSmartCollectionClick()
                            },
                            text = {
                                Text(stringResource(Res.string.collection_label_smart_collection))
                            },
                            icon = {
                                Icon(
                                    imageVector = ComicIcons.CollectionsBookmark,
                                    contentDescription = null
                                )
                            }
                        )
                        Spacer(Modifier.size(ComicTheme.dimension.padding))
                    }
                }
                ExtendedFloatingActionButton(
                    expanded = !show,
                    text = { Text(stringResource(Res.string.collection_label_create)) },
                    icon = {
                        val rotate by animateFloatAsState(if (show) 225f else 0f)
                        Icon(
                            imageVector = ComicIcons.Add,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotate)
                        )
                    },
                    containerColor = containerColor,
                    onClick = { show = !show }
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = LocalContainerColor.current,
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        CollectionListContents(
            lazyPagingItems = lazyPagingItems,
            onAction = onContentsAction,
            lazyListState = lazyListState,
            contentPadding = innerPadding,
        )
    }
}
