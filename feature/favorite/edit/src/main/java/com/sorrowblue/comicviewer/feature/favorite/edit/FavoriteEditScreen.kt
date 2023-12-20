package com.sorrowblue.comicviewer.feature.favorite.edit

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.feature.favorite.edit.navigation.FavoriteEditArgs
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawNoData
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.SavableState
import com.sorrowblue.comicviewer.framework.ui.material3.ElevationTokens
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile
import com.sorrowblue.comicviewer.framework.ui.rememberSavableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
internal fun FavoriteEditRoute(
    args: FavoriteEditArgs,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
    state: FavoriteEditScreenState = rememberFavoriteEditScreenState(args),
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
)

@OptIn(SavedStateHandleSaveableApi::class)
@Stable
internal class FavoriteEditScreenState(
    scope: CoroutineScope,
    override val savedStateHandle: SavedStateHandle,
    private val args: FavoriteEditArgs,
    private val viewModel: FavoriteEditViewModel,
) : SavableState {

    var uiState by savedStateHandle.saveable { mutableStateOf(FavoriteEditScreenUiState()) }
        private set

    val pagingDataFlow = viewModel.pagingDataFlow(args.favoriteId)

    init {
        scope.launch {
            viewModel.getFavoriteUseCase.execute(GetFavoriteUseCase.Request(args.favoriteId))
                .first().dataOrNull?.let {
                    uiState = uiState.copy(name = it.name)

                }
        }
    }

    fun onDeleteClick(file: File) {
        viewModel.removeFile(args.favoriteId, file)
    }

    fun onNameChange(name: String) {
        uiState = uiState.copy(name = name)
    }

    fun onSaveClick(onComplete: () -> Unit) {
        viewModel.save(args.favoriteId, uiState.name, onComplete)
    }
}

@Composable
internal fun rememberFavoriteEditScreenState(
    args: FavoriteEditArgs,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FavoriteEditViewModel = hiltViewModel(),
) = rememberSavableState { savedStateHandle ->
    FavoriteEditScreenState(
        args = args,
        scope = scope,
        savedStateHandle = savedStateHandle,
        viewModel = viewModel
    )
}

@Composable
internal fun containerColor(colorTransitionFraction: Float): Color {
    return lerp(
        ComicTheme.colorScheme.surface,
        MaterialTheme.colorScheme.surfaceColorAtElevation(ElevationTokens.Level2),
        FastOutLinearInEasing.transform(colorTransitionFraction)
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun FavoriteEditDialog(
    uiState: FavoriteEditScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onNameChange: (String) -> Unit,
    onDeleteClick: (File) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.padding(vertical = 16.dp),
        title = {
            Column {
                Text(text = stringResource(id = R.string.favorite_edit_title))
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ComicTheme.dimension.margin)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ComicTheme.dimension.margin)
                ) {
                    val colorTransitionFraction = scrollBehavior.state.overlappedFraction
                    val animateAlpha by animateFloatAsState(
                        targetValue = if (colorTransitionFraction > 0.01f) 1f else 0f,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "DropdownMenuChipColorAnimation"
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .alpha(animateAlpha)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
        text = {
            Box(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                if (lazyPagingItems.isEmptyData) {
                    EmptyContent(
                        imageVector = ComicIcons.UndrawNoData,
                        text = stringResource(R.string.favorite_edit_text_no_favorites),
                        contentPadding = PaddingValues()
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(),
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
                                    modifier = Modifier
                                        .animateItemPlacement()
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteEditScreen(
    uiState: FavoriteEditScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDeleteClick: (File) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
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
                    scrollBehavior = scrollBehavior
                )
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ComicTheme.dimension.margin)
                )
                Spacer(modifier = Modifier.size(ComicTheme.dimension.margin))
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(
                imageVector = ComicIcons.UndrawNoData,
                text = stringResource(R.string.favorite_edit_text_no_favorites),
                contentPadding = contentPadding
            )
        } else {
            LazyColumn(contentPadding = contentPadding) {
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

@Preview
@Composable
private fun PreviewFavoriteEditScreen() {
    val files = List(20) {
        BookFile(BookshelfId(it), "Name $it", "", "", 0, 0, "", 0, 0, 0)
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
