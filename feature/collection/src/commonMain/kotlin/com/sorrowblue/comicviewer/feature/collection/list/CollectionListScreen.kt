package com.sorrowblue.comicviewer.feature.collection.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.feature.collection.section.CollectionListAppBar
import com.sorrowblue.comicviewer.feature.collection.section.CollectionListContents
import com.sorrowblue.comicviewer.feature.collection.section.CollectionListContentsAction
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldLayout
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.canonical.PrimaryActionButtonMenu
import comicviewer.feature.collection.generated.resources.Res
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
        scaffoldState = state.scaffoldState,
        lazyPagingItems = state.lazyPagingItems,
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
internal fun CollectionListScreen(
    scaffoldState: CanonicalScaffoldState<Unit>,
    lazyPagingItems: LazyPagingItems<Collection>,
    lazyListState: LazyListState,
    onContentsAction: (CollectionListContentsAction) -> Unit,
    onSettingsClick: () -> Unit,
    onCreateBasicCollectionClick: () -> Unit,
    onCreateSmartCollectionClick: () -> Unit,
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    scaffoldState.appBarState.scrollBehavior = appBarScrollBehavior
    scaffoldState.CanonicalScaffoldLayout(
        topBar = {
            CollectionListAppBar(
                onSettingsClick = onSettingsClick,
            )
        },
        forcePrimaryActionPosition = true,
        primaryActionContent = {
            PrimaryActionButtonMenu {
                FloatingActionButtonMenuItem(
                    onClick = {
                        scaffoldState.toggleFabMenu()
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
                FloatingActionButtonMenuItem(
                    onClick = {
                        scaffoldState.toggleFabMenu()
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
            }
        },
    ) { contentPadding ->
        CollectionListContents(
            lazyPagingItems = lazyPagingItems,
            onAction = onContentsAction,
            lazyListState = lazyListState,
            contentPadding = contentPadding,
        )
    }
}
