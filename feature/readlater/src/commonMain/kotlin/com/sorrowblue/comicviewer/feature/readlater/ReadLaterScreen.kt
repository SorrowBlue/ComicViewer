package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterTopAppBar
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawSaveBookmarks
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import comicviewer.feature.readlater.generated.resources.Res
import comicviewer.feature.readlater.generated.resources.readlater_label_nothing_to_read_later
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AdaptiveNavigationSuiteScaffoldState.ReadLaterScreen(
    lazyPagingItems: LazyPagingItems<File>,
    lazyGridState: LazyGridState,
    onClearAllClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    modifier: Modifier = Modifier,
) {
    AdaptiveNavigationSuiteScaffold(
        modifier = modifier,
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            topBar = {
                ReadLaterTopAppBar(
                    scrollBehavior = scrollBehavior,
                    onClearAllClick = onClearAllClick,
                    onSettingsClick = onSettingsClick,
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { contentPadding ->
            ReadLaterContents(
                lazyPagingItems = lazyPagingItems,
                lazyGridState = lazyGridState,
                onItemClick = onFileClick,
                onItemInfoClick = onFileInfoClick,
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
private fun ReadLaterContents(
    lazyPagingItems: LazyPagingItems<File>,
    lazyGridState: LazyGridState,
    onItemClick: (File) -> Unit,
    onItemInfoClick: (File) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContent(
            imageVector = ComicIcons.UndrawSaveBookmarks,
            text = stringResource(Res.string.readlater_label_nothing_to_read_later),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        )
    } else {
        FileLazyVerticalGrid(
            uiState = FileLazyVerticalGridUiState(fileListDisplay = FileListDisplay.List),
            lazyPagingItems = lazyPagingItems,
            contentPadding = contentPadding + PaddingValues(16.dp),
            onItemClick = onItemClick,
            onItemInfoClick = onItemInfoClick,
            state = lazyGridState,
            modifier = Modifier
                .fillMaxSize(),
        )
    }
}
