package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.result.NavResultReceiver
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoSheet
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoSheetNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDelete
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequest
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequestResult
import com.sorrowblue.comicviewer.feature.bookshelf.section.BookshelfSheet
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldLayout
import com.sorrowblue.comicviewer.framework.ui.NavigationSuiteScaffold2State
import com.sorrowblue.comicviewer.framework.ui.canonical.CanonicalAppBar
import com.sorrowblue.comicviewer.framework.ui.canonical.PrimaryActionButton
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import comicviewer.feature.bookshelf.generated.resources.Res
import comicviewer.feature.bookshelf.generated.resources.bookshelf_btn_add
import comicviewer.feature.bookshelf.generated.resources.bookshelf_label_bookshelf
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Serializable
internal data object Bookshelf

internal interface BookshelfScreenNavigator : BookshelfInfoSheetNavigator {
    fun onSettingsClick()
    fun onFabClick()
    fun onBookshelfClick(bookshelfId: BookshelfId, path: String)
}

@Destination<Bookshelf>
@Composable
internal fun BookshelfScreen(
    deleteNavResultReceiver: NavResultReceiver<BookshelfDelete, Boolean>,
    notificationNavResultReceiver: NavResultReceiver<NotificationRequest, NotificationRequestResult>,
    navigator: BookshelfScreenNavigator = koinInject(),
    state: BookshelfScreenState = rememberBookshelfScreenState(),
) {
    BookshelfScreen(
        scaffoldState = state.scaffoldState,
        lazyPagingItems = state.pagingItems,
        lazyGridState = state.lazyGridState,
        onFabClick = navigator::onFabClick,
        onSettingsClick = navigator::onSettingsClick,
        onBookshelfClick = navigator::onBookshelfClick,
        onBookshelfInfoClick = state::onBookshelfInfoClick,
    ) { contentKey ->
        BookshelfInfoSheet(
            bookshelfId = contentKey,
            onCloseClick = state::onSheetCloseClick,
            navigator = navigator,
            snackbarHostState = state.scaffoldState.snackbarHostState,
            deleteNavResultReceiver = deleteNavResultReceiver,
            notificationNavResultReceiver = notificationNavResultReceiver,
        )
    }
}

@Composable
internal fun BookshelfScreen(
    scaffoldState: NavigationSuiteScaffold2State<BookshelfId>,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    onFabClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    extraPane: @Composable (BookshelfId) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val expanded by rememberLastScrolledForward(lazyGridState)
    scaffoldState.appBarState.scrollBehavior = scrollBehavior
    scaffoldState.CanonicalScaffoldLayout(
        topBar = {
            CanonicalAppBar(
                title = { Text(text = stringResource(Res.string.bookshelf_label_bookshelf)) },
                actions = { SettingsIconButton(onClick = onSettingsClick) },
            )
        },
        extraPane = { extraPane(it) },
        primaryActionContent = {
            PrimaryActionButton(
                visible = expanded,
                text = { Text(text = stringResource(Res.string.bookshelf_btn_add)) },
                icon = { Icon(imageVector = ComicIcons.Add, contentDescription = null) },
                onClick = onFabClick
            )
        },
        modifier = modifier
    ) { contentPadding ->
        val pad = contentPadding.plus(
            PaddingValues(
                start = if (scaffoldState.navigationSuiteType.isNavigationRail && scaffoldState.suiteScaffoldState.targetValue == NavigationSuiteScaffoldValue.Visible) {
                    0.dp
                } else {
                    ComicTheme.dimension.margin
                },
                end = if (scaffoldState.navigator.scaffoldDirective.maxHorizontalPartitions == 1 || scaffoldState.navigator.scaffoldValue.tertiary != PaneAdaptedValue.Expanded) {
                    ComicTheme.dimension.margin
                } else {
                    0.dp
                }
            )
        )
        BookshelfSheet(
            lazyPagingItems = lazyPagingItems,
            lazyGridState = lazyGridState,
            onBookshelfClick = onBookshelfClick,
            onBookshelfInfoClick = onBookshelfInfoClick,
            contentPadding = pad
        )
    }
}

@Composable
private fun rememberLastScrolledForward(
    lazyGridState: LazyGridState,
    delay: Long = 300,
): State<Boolean> {
    val expanded = remember { mutableStateOf(true) }
    LaunchedEffect(lazyGridState.lastScrolledForward) {
        delay(delay)
        expanded.value = !lazyGridState.lastScrolledForward
    }
    return expanded
}
