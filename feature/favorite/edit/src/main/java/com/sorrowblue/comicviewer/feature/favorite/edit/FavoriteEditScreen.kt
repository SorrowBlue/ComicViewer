package com.sorrowblue.comicviewer.feature.favorite.edit

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.fakeBookFile
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteNameTextField
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawNoData
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.material3.ElevationTokens
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface FavoriteEditScreenNavigator {
    fun navigateUp()
    fun onComplete()
}

class FavoriteEditArgs(val favoriteId: FavoriteId)

@Destination<ExternalModuleGraph>(navArgs = FavoriteEditArgs::class)
@Composable
internal fun FavoriteEditScreen(args: FavoriteEditArgs, navigator: FavoriteEditScreenNavigator) {
    FavoriteEditScreen(
        args = args,
        onBackClick = navigator::navigateUp,
        onComplete = navigator::onComplete
    )
}

@Composable
private fun FavoriteEditScreen(
    args: FavoriteEditArgs,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
    state: FavoriteEditScreenState = rememberFavoriteEditScreenState(args = args),
) {
    val uiState = state.uiState
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val isCompact = rememberMobile()
    if (isCompact) {
        FavoriteEditScreen(
            uiState = uiState,
            lazyPagingItems = lazyPagingItems,
            onBackClick = onBackClick,
            onSaveClick = { state.onSaveClick(onComplete) },
            onNameChange = state::onNameChange,
            onDeleteClick = state::onDeleteClick
        )
    } else {
        FavoriteEditDialog(
            uiState = uiState,
            lazyPagingItems = lazyPagingItems,
            onNameChange = state::onNameChange,
            onDeleteClick = state::onDeleteClick,
            onDismissRequest = onBackClick,
            onConfirmClick = { state.onSaveClick(onComplete) }
        )
    }
}

data class FavoriteEditScreenUiState(
    val name: String = "",
    val nameError: Boolean = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteEditScreen(
    uiState: FavoriteEditScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDeleteClick: (File) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            val colorTransitionFraction = scrollBehavior.state.overlappedFraction
            val appBarContainerColor by animateColorAsState(
                targetValue = containerColor(if (colorTransitionFraction > 0.01f) 1f else 0f),
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "DropdownMenuChipColorAnimation"
            )
            Column(Modifier.background(appBarContainerColor)) {
                TopAppBar(
                    modifier = Modifier.wrapContentHeight(),
                    title = { Text(stringResource(R.string.favorite_edit_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(ComicIcons.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton(onClick = onSaveClick) {
                            Icon(ComicIcons.Save, null)
                        }
                    },
                    windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
                    scrollBehavior = scrollBehavior
                )
                FavoriteNameTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    isError = uiState.nameError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ComicTheme.dimension.margin),
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(
                imageVector = ComicIcons.UndrawNoData,
                text = stringResource(R.string.favorite_edit_text_no_favorites),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            )
        } else {
            LazyColumn(
                state = lazyListState,
                contentPadding = contentPadding,
                modifier = Modifier.drawVerticalScrollbar(lazyListState)
            ) {
                items(
                    lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { "${it.bookshelfId.value}${it.path}" }
                ) {
                    val item = lazyPagingItems[it]
                    if (item != null) {
                        ListItem(
                            headlineContent = { Text(item.name) },
                            leadingContent = {
                                AsyncImage(model = item, null, Modifier.size(56.dp))
                            },
                            trailingContent = {
                                IconButton(onClick = { onDeleteClick(item) }) {
                                    Icon(ComicIcons.Delete, null)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteEditDialog(
    uiState: FavoriteEditScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onNameChange: (String) -> Unit,
    onDeleteClick: (File) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.padding(vertical = ComicTheme.dimension.margin),
        title = {
            Column {
                Text(text = stringResource(id = R.string.favorite_edit_title))
                Spacer(modifier = Modifier.size(ComicTheme.dimension.minPadding * 2))
                FavoriteNameTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    isError = uiState.nameError,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(text = stringResource(R.string.favorite_edit_action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
        text = {
            Column(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                if (lazyPagingItems.isEmptyData) {
                    EmptyContent(
                        imageVector = ComicIcons.UndrawNoData,
                        text = stringResource(R.string.favorite_edit_text_no_favorites),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    HorizontalDivider(Modifier.alpha(if (lazyListState.canScrollBackward) 1f else 0f))
                    LazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(),
                        modifier = Modifier.drawVerticalScrollbar(lazyListState)
                    ) {
                        items(
                            lazyPagingItems.itemCount,
                            key = lazyPagingItems.itemKey { "${it.bookshelfId.value}${it.path}" }
                        ) {
                            val item = lazyPagingItems[it]
                            if (item != null) {
                                ListItem(
                                    headlineContent = { Text(item.name) },
                                    leadingContent = {
                                        AsyncImage(model = item, null, Modifier.size(56.dp))
                                    },
                                    trailingContent = {
                                        IconButton(onClick = { onDeleteClick(item) }) {
                                            Icon(ComicIcons.Delete, null)
                                        }
                                    },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                    HorizontalDivider(Modifier.alpha(if (lazyListState.canScrollForward) 1f else 0f))
                }
            }
        }
    )
}

@Composable
private fun containerColor(colorTransitionFraction: Float): Color {
    return lerp(
        ComicTheme.colorScheme.surface,
        MaterialTheme.colorScheme.surfaceColorAtElevation(ElevationTokens.Level2),
        FastOutLinearInEasing.transform(colorTransitionFraction)
    )
}

@Preview
@Composable
private fun PreviewFavoriteEditScreen() {
    val files = List(20) {
        fakeBookFile(BookshelfId(it))
    }
    val a: Flow<PagingData<File>> = flowOf(PagingData.from(files))
    ComicTheme {
        FavoriteEditScreen(
            FavoriteEditScreenUiState(),
            a.collectAsLazyPagingItems(),
            {},
            {},
            {},
            {}
        )
    }
}

@Preview
@Composable
private fun PreviewFavoriteEditDialog() {
    val files = List(20) {
        fakeBookFile(BookshelfId(it))
    }
    val a: Flow<PagingData<File>> = flowOf(PagingData.from(files))
    ComicTheme {
        FavoriteEditDialog(
            FavoriteEditScreenUiState(),
            a.collectAsLazyPagingItems(),
            {},
            {},
            {},
            {}
        )
    }
}
