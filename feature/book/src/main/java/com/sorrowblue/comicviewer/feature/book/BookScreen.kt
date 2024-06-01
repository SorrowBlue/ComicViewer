package com.sorrowblue.comicviewer.feature.book

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.navigation.BookGraph
import com.sorrowblue.comicviewer.feature.book.navigation.BookGraphTransitions
import com.sorrowblue.comicviewer.feature.book.section.BookBottomBar
import com.sorrowblue.comicviewer.feature.book.section.BookSheet
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import com.sorrowblue.comicviewer.feature.book.section.PageFormat2
import com.sorrowblue.comicviewer.feature.book.section.PageItem
import com.sorrowblue.comicviewer.feature.book.section.PageScale
import com.sorrowblue.comicviewer.feature.book.section.UnratedPage
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.ElevationTokens
import com.sorrowblue.comicviewer.framework.ui.material3.ExposedDropdownMenu
import kotlinx.collections.immutable.toPersistentList

@NavTypeSerializer
internal class BookshelfIdSerializer : DestinationsNavTypeSerializer<BookshelfId> {
    override fun toRouteString(value: BookshelfId) = value.value.toString()
    override fun fromRouteString(routeStr: String) = BookshelfId(routeStr.toInt())
}

internal sealed interface BookScreenUiState {

    data class Loading(val name: String) : BookScreenUiState

    data class Error(val name: String) : BookScreenUiState

    data class Loaded(
        val book: Book,
        val bookSheetUiState: BookSheetUiState,
        val isVisibleTooltip: Boolean = true,
        val isShowBookMenu: Boolean = false,
    ) : BookScreenUiState
}

interface BookScreenNavigator {
    fun navigateUp()
    fun onSettingsClick()
    fun onNextBookClick(book: Book, favoriteId: FavoriteId)
}

class BookArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val name: String,
    val favoriteId: FavoriteId = FavoriteId.Default,
)

@Destination<BookGraph>(
    start = true,
    navArgs = BookArgs::class,
    style = BookGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun BookScreen(
    args: BookArgs,
    navigator: BookScreenNavigator,
) {
    BookScreen(
        args = args,
        onBackClick = navigator::navigateUp,
        onSettingsClick = navigator::onSettingsClick,
        onNextBookClick = navigator::onNextBookClick
    )
}

@Composable
private fun BookScreen(
    args: BookArgs,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNextBookClick: (Book, FavoriteId) -> Unit,
    state: BookScreenState = rememberBookScreenState(args = args),
) {
    when (val uiState = state.uiState) {
        is BookScreenUiState.Loading ->
            BookLoadingScreen(
                uiState = uiState,
                onBackClick = onBackClick
            )

        is BookScreenUiState.Error ->
            BookErrorScreen(
                uiState = uiState,
                onBackClick = onBackClick
            )

        is BookScreenUiState.Loaded -> {
            val state2 = rememberBookScreenState2(args, uiState)
            val uiState2 = state2.uiState
            BookScreen(
                uiState = uiState2,
                pagerState = state2.pagerState,
                currentList = state2.currentList,
                onBackClick = onBackClick,
                onNextBookClick = { onNextBookClick(it, args.favoriteId) },
                onContainerClick = state2::toggleTooltip,
                onContainerLongClick = state2::onContainerLongClick,
                onPageChange = state2::onPageChange,
                onSettingsClick = onSettingsClick,
                onPageLoad = state2::onPageLoaded,
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookScreen(
    uiState: BookScreenUiState.Loaded,
    pagerState: PagerState,
    currentList: SnapshotStateList<PageItem>,
    onBackClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
    onContainerClick: () -> Unit,
    onContainerLongClick: () -> Unit,
    onPageChange: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onPageLoad: (UnratedPage, Bitmap) -> Unit,
) {
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = uiState.isVisibleTooltip,
                enter = slideInVertically { -it },
                exit = slideOutVertically { -it }
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = uiState.book.name,
                            modifier = Modifier.basicMarquee()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = ComicIcons.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            elevation = ElevationTokens.Level2
                        )
                    ),
                    windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
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
        contentWindowInsets = WindowInsets.safeDrawing
    ) { _ ->
        BookSheet(
            uiState = uiState.bookSheetUiState,
            pagerState = pagerState,
            pages = currentList,
            onClick = onContainerClick,
            onLongClick = onContainerLongClick,
            onNextBookClick = onNextBookClick,
            onPageLoad = onPageLoad
        )
    }
}

data class BookMenuSheetUiState(
    val pageFormat2: PageFormat2 = PageFormat2.Default,
    val pageScale: PageScale = PageScale.Fit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookMenuSheet(
    uiState: BookMenuSheetUiState,
    onDismissRequest: () -> Unit,
    onChangeValue: (PageFormat2) -> Unit,
    onChangeValue2: (PageScale) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        contentWindowInsets = { PaddingValues().asWindowInsets() }
    ) {
        ExposedDropdownMenu(
            label = stringResource(id = R.string.book_label_display_format),
            value = stringResource(id = uiState.pageFormat2.label),
            onChangeValue = onChangeValue,
            menus = remember(PageFormat2.entries::toPersistentList),
        )
        ExposedDropdownMenu(
            label = stringResource(id = R.string.book_label_scale),
            value = stringResource(id = uiState.pageScale.label),
            onChangeValue = onChangeValue2,
            menus = remember(PageScale.entries::toPersistentList),
        )
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}
