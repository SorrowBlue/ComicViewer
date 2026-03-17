package com.sorrowblue.comicviewer.feature.collection.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.feature.collection.section.CollectionList
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveAppBar
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.LocalNavigationItems
import com.sorrowblue.comicviewer.framework.ui.adaptive.NavigationItems
import com.sorrowblue.comicviewer.framework.ui.adaptive.PrimaryActionButtonMenu
import com.sorrowblue.comicviewer.framework.ui.adaptive.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
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
    modifier: Modifier = Modifier,
) {
    AdaptiveNavigationSuiteScaffold(modifier = modifier) {
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
}

@Preview
@Composable
private fun CollectionScreenPreview() {
    CompositionLocalProvider(
        LocalNavigationItems provides object : NavigationItems {
            @Composable
            override fun Content(onNavigationReSelect: () -> Unit) {
                NavigationSuiteItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(ComicIcons.Favorite, null)
                    },
                    label = null,
                )
                NavigationSuiteItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(ComicIcons.Favorite, null)
                    },
                    label = null,
                )
                NavigationSuiteItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(ComicIcons.Favorite, null)
                    },
                    label = null,
                )
                NavigationSuiteItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(ComicIcons.Favorite, null)
                    },
                    label = null,
                )
            }
        },
    ) {
        PreviewTheme {
            val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
            scaffoldState.CollectionListScreen(
                lazyPagingItems = PagingData.flowData<Collection> { fakeBasicCollection(it) }
                    .collectAsLazyPagingItems(),
                lazyListState = rememberLazyListState(),
                onItemClick = {},
                onEditClick = {},
                onDeleteClick = { },
                onSettingsClick = {},
                onCreateBasicCollectionClick = {},
                onCreateSmartCollectionClick = {},
            )
        }
    }
}
