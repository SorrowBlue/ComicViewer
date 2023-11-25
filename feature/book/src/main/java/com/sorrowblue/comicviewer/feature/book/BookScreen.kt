package com.sorrowblue.comicviewer.feature.book

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.navigation.BookArgs
import com.sorrowblue.comicviewer.feature.book.section.BookBottomBar
import com.sorrowblue.comicviewer.feature.book.section.BookItem
import com.sorrowblue.comicviewer.feature.book.section.BookSheet
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import com.sorrowblue.comicviewer.feature.book.section.PageScale
import com.sorrowblue.comicviewer.feature.book.section.UnratedPage
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.ElevationTokens
import com.sorrowblue.comicviewer.framework.ui.material3.ListItemSwitch
import com.sorrowblue.comicviewer.framework.ui.material3.Text
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

internal sealed interface BookScreenUiState {

    data class Loading(val name: String) : BookScreenUiState

    data class Error(val name: String) : BookScreenUiState

    data class Loaded(
        val book: Book,
        val prevBook: Book?,
        val nextBook: Book?,
        val bookSheetUiState: BookSheetUiState,
        val isVisibleTooltip: Boolean = true,
        val isShowBookMenu: Boolean = false,
    ) : BookScreenUiState
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BookRoute(
    args: BookArgs,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
    contentPadding: PaddingValues,
    state: BookScreenState = rememberBookScreenState(args = args),
) {
    when (val uiState = state.uiState) {
        is BookScreenUiState.Loading ->
            BookLoadingScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                contentPadding = contentPadding
            )

        is BookScreenUiState.Error ->
            BookErrorScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                contentPadding = contentPadding
            )

        is BookScreenUiState.Loaded -> {
            val state2 = rememberBookScreenState2(args, uiState)
            val uiState2 = state2.uiState
            BookScreen(
                uiState = uiState2,
                pagerState = state2.pagerState,
                currentList = state2.currentList,
                onBackClick = onBackClick,
                onNextBookClick = onNextBookClick,
                onContainerClick = state2::toggleTooltip,
                onContainerLongClick = state2::onContainerLongClick,
                onPageChange = state2::onPageChange,
                onSettingsClick = onSettingsClick,
                contentPadding = contentPadding,
                onPageLoaded = state2::onPageLoaded,
            )
            if (uiState2.isShowBookMenu) {
                BookMenuSheet(
                    uiState = state2.bookMenuSheetUiState,
                    onDismissRequest = state2::hideBookMenu,
                    onChangeValue = state2::onChangePageDisplayFormat,
                    onChangeValue2 = state2::onChangePageScale
                )
            }
            DisposableEffect(Unit) {
                onDispose(state2::onScreenDispose)
            }
            LifecycleEffect(targetEvent = Lifecycle.Event.ON_STOP, action = state2::onStop)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookScreen(
    uiState: BookScreenUiState.Loaded,
    pagerState: PagerState,
    currentList: SnapshotStateList<BookItem>,
    onBackClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
    onContainerClick: () -> Unit,
    onContainerLongClick: () -> Unit,
    onPageChange: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onPageLoaded: (UnratedPage, Bitmap) -> Unit,
    contentPadding: PaddingValues,
) {
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = uiState.isVisibleTooltip,
                enter = slideInVertically { -it },
                exit = slideOutVertically { -it }
            ) {
                TopAppBar(
                    title = uiState.book.name,
                    onBackClick = onBackClick,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            elevation = ElevationTokens.Level2
                        )
                    ),
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                        }
                    }
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = uiState.isVisibleTooltip,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                BookBottomBar(
                    pageRange = 1f..uiState.book.totalPageCount.toFloat(),
                    currentPage = pagerState.currentPage,
                    onPageChange = onPageChange
                )
            }
        },
        contentWindowInsets = contentPadding.asWindowInsets()
    ) { innerPadding ->
        BookSheet(
            uiState = uiState.bookSheetUiState,
            pagerState = pagerState,
            pages = currentList,
            onClick = onContainerClick,
            onLongClick = onContainerLongClick,
            onNextBookClick = onNextBookClick,
            onPageLoaded = onPageLoaded
        )
    }
}

data class BookMenuSheetUiState(
    val pageDisplayFormat: PageDisplayFormat = PageDisplayFormat.Default,
    val pageScale: PageScale = PageScale.Fit,
)

enum class PageDisplayFormat(override val label: Int) : Menu3 {
    Default(R.string.book_label_display_format_default),
    Split(R.string.book_label_display_format_split),
    Spread(R.string.book_label_display_format_spread),
    SplitSpread(R.string.book_label_display_format_splitspread)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookMenuSheet(
    uiState: BookMenuSheetUiState,
    onDismissRequest: () -> Unit,
    onChangeValue: (PageDisplayFormat) -> Unit,
    onChangeValue2: (PageScale) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        windowInsets = PaddingValues().asWindowInsets()
    ) {
        Menu3(
            label = stringResource(id = R.string.book_label_display_format),
            value = stringResource(id = uiState.pageDisplayFormat.label),
            onChangeValue = onChangeValue,
            menus = remember(PageDisplayFormat.entries::toPersistentList),
        )
        Menu3(
            label = stringResource(id = R.string.book_label_scale),
            value = stringResource(id = uiState.pageScale.label),
            onChangeValue = onChangeValue2,
            menus = remember(PageScale.entries::toPersistentList),
        )
        ListItem(headlineContent = { Text(text = "表示レイアウト") })
        ListItemSwitch(
            headlineContent = { Text(text = "余白を切り取る") },
            checked = true,
            onCheckedChange = {})
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

interface Menu3 {
    val label: Int
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Menu3> Menu3(
    label: String,
    value: String,
    onChangeValue: (T) -> Unit,
    menus: PersistentList<T>,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            readOnly = true,
            value = value,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            menus.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption.label) },
                    onClick = {
                        onChangeValue(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
