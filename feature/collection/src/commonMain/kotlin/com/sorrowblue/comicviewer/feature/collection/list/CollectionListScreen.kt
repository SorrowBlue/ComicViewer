/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavEdge
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionCreateNavKey
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionEditNavKey
import com.sorrowblue.comicviewer.feature.collection.nav.SmartCollectionCreateNavKey
import com.sorrowblue.comicviewer.feature.collection.nav.SmartCollectionEditNavKey
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionDeleteNavKey
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionListNavKey
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionNavKey
import com.sorrowblue.comicviewer.feature.collection.section.CollectionList
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveAppBar
import com.sorrowblue.comicviewer.framework.ui.adaptive.PrimaryActionButtonMenu
import com.sorrowblue.comicviewer.framework.ui.adaptive.currentNavigationSuiteType
import com.sorrowblue.comicviewer.framework.ui.adaptive.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBasicCollection
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_label_collection
import comicviewer.feature.collection.generated.resources.collection_label_smart_collection
import comicviewer.feature.collection.generated.resources.collection_title
import org.jetbrains.compose.resources.stringResource

@NavEdge(to = CollectionNavKey::class)
@NavEdge(to = BasicCollectionCreateNavKey::class)
@NavEdge(to = SmartCollectionCreateNavKey::class)
@NavEdge(to = BasicCollectionEditNavKey::class)
@NavEdge(to = SmartCollectionEditNavKey::class)
@NavEdge(to = CollectionDeleteNavKey::class)
@NavDestination(CollectionListNavKey::class)
@Composable
internal fun CollectionListScreen(
    lazyPagingItems: LazyPagingItems<Collection>,
    lazyListState: LazyListState,
    onItemClick: (Collection) -> Unit,
    onEditClick: (Collection) -> Unit,
    onDeleteClick: (Collection) -> Unit,
    onSettingsClick: () -> Unit,
    onCreateBasicCollectionClick: () -> Unit,
    onCreateSmartCollectionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigationSuiteType = currentNavigationSuiteType()
    val fabSize = remember(navigationSuiteType.isNavigationRail) {
        if (navigationSuiteType.isNavigationRail) {
            ToggleFloatingActionButtonDefaults
                .containerSizeMedium()
        } else {
            ToggleFloatingActionButtonDefaults
                .containerSize()
        }.invoke(0f) + 16.dp
    }
    Scaffold(
        topBar = {
            AdaptiveAppBar(
                title = {
                    Text(text = stringResource(Res.string.collection_title))
                },
                actions = {
                    SettingsIconButton(onClick = onSettingsClick)
                },
            )
        },
        floatingActionButton = {
            PrimaryActionButtonMenu(
                modifier = Modifier.testTag("FloatingActionButton"),
            ) {
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
                    modifier = Modifier.testTag("BasicCollectionCreateButton"),
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
                    modifier = Modifier.testTag("SmartCollectionCreateButton"),
                )
            }
        },
        modifier = modifier,
    ) { contentPadding ->
        CollectionList(
            lazyPagingItems = lazyPagingItems,
            onItemClick = onItemClick,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
            lazyListState = lazyListState,
            contentPadding = contentPadding + PaddingValues(bottom = fabSize),
        )
    }
}

@NavPreview(CollectionListNavKey::class, primary = true)
@Preview
@Composable
private fun CollectionScreenPreview() = PreviewTheme {
    CollectionListScreen(
        lazyPagingItems = PagingData.flowData<Collection> { fakeBasicCollection(it) }
            .collectAsLazyPagingItems(),
        lazyListState = rememberLazyListState(),
        onItemClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onSettingsClick = {},
        onCreateBasicCollectionClick = {},
        onCreateSmartCollectionClick = {},
    )
}
