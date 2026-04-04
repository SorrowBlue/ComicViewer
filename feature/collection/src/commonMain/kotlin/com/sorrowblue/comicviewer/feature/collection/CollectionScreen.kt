package com.sorrowblue.comicviewer.feature.collection

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.collection.section.CollectionAppBar
import com.sorrowblue.comicviewer.feature.collection.section.CollectionAppBarUiState
import com.sorrowblue.comicviewer.feature.collection.section.CollectionContents
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.LocalNavigationItems
import com.sorrowblue.comicviewer.framework.ui.adaptive.NavigationItems
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData

@Composable
internal fun AdaptiveNavigationSuiteScaffoldState.CollectionScreen(
    uiState: CollectionScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    lazyGridState: LazyGridState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    modifier: Modifier = Modifier,
) {
    AdaptiveNavigationSuiteScaffold(modifier = modifier) {
        Scaffold(
            topBar = {
                CollectionAppBar(
                    uiState = uiState.appBarUiState,
                    onBackClick = onBackClick,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    onSettingsClick = onSettingsClick,
                )
            },
        ) { contentPadding ->
            CollectionContents(
                fileLazyVerticalGridUiState = uiState.fileLazyVerticalGridUiState,
                lazyPagingItems = lazyPagingItems,
                lazyGridState = lazyGridState,
                onItemClick = onFileClick,
                onItemInfoClick = onFileInfoClick,
                contentPadding = contentPadding + PaddingValues(16.dp),
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
            scaffoldState.CollectionScreen(
                uiState = remember {
                    CollectionScreenUiState(
                        appBarUiState = CollectionAppBarUiState(title = "Collection Preview"),
                    )
                },
                lazyPagingItems = PagingData.flowData<File> { fakeBookFile(it) }
                    .collectAsLazyPagingItems(),
                lazyGridState = rememberLazyGridState(),
                onBackClick = {},
                onEditClick = {},
                onDeleteClick = {},
                onSettingsClick = {},
                onFileClick = {},
                onFileInfoClick = {},
            )
        }
    }
}
