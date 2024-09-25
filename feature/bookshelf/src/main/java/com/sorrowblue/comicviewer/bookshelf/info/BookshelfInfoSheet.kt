package com.sorrowblue.comicviewer.bookshelf.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.SubcomposeAsyncImage
import com.ramcosta.composedestinations.result.ResultRecipient
import com.sorrowblue.comicviewer.bookshelf.component.BookshelfConverter.source
import com.sorrowblue.comicviewer.bookshelf.section.NotificationRequestResult
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.feature.bookshelf.destinations.NotificationRequestDialogDestination
import com.sorrowblue.comicviewer.feature.bookshelf.remove.destinations.BookshelfRemoveDialogDestination
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.ImageDelete
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.ImageSync
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.ShelvesSync
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme2
import com.sorrowblue.comicviewer.framework.ui.preview.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fakeSmbServer
import com.sorrowblue.comicviewer.framework.ui.preview.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.previewPainter

data class BookshelfInfoSheetUiState(
    val bookshelf: Bookshelf,
    val folder: Folder,
    val enabled: Boolean = true,
    val isProgressScan: Boolean = false,
)

sealed interface BookshelfInfoSheetAction {
    data object Remove : BookshelfInfoSheetAction
    data object Edit : BookshelfInfoSheetAction
    data object Scan : BookshelfInfoSheetAction
    data object ThumbnailRegeneration : BookshelfInfoSheetAction
    data object Close : BookshelfInfoSheetAction
}

internal interface BookshelfInfoSheetNavigator {
    fun notificationRequest()
    fun edit(id: BookshelfId)
    fun remove(bookshelf: Bookshelf)
}

@Composable
internal fun BookshelfInfoSheet(
    content: BookshelfFolder,
    snackbarHostState: SnackbarHostState,
    scaffoldNavigator: ThreePaneScaffoldNavigator<BookshelfFolder>,
    navigator: BookshelfInfoSheetNavigator,
    removeDialogResultRecipient: ResultRecipient<BookshelfRemoveDialogDestination, Boolean>,
    notificationResultRecipient: ResultRecipient<NotificationRequestDialogDestination, NotificationRequestResult>,
) {
    val state = rememberBookshelfInfoSheetState(
        bookshelfFolder = content,
        snackbarHostState = snackbarHostState,
        navigator = scaffoldNavigator
    )
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    BookshelfInfoSheet(
        uiState = state.uiState,
        onAction = state::onAction,
        lazyPagingItems = lazyPagingItems,
    )

    removeDialogResultRecipient.onNavResult(state::onRemoveResult)

    notificationResultRecipient.onNavResult(state::onNotificationResult)

    LaunchedEventEffect(state.event) {
        when (it) {
            is BookshelfInfoSheetStateEvent.Edit -> navigator.edit(it.id)
            is BookshelfInfoSheetStateEvent.ShowRequestPermissionRationale -> navigator.notificationRequest()
            is BookshelfInfoSheetStateEvent.Remove -> navigator.remove(it.bookshelf)
        }
    }
}

@Composable
private fun BookshelfInfoSheet(
    uiState: BookshelfInfoSheetUiState,
    lazyPagingItems: LazyPagingItems<BookThumbnail>,
    onAction: (BookshelfInfoSheetAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    CanonicalExtraPaneScaffold(
        title = { Text(text = stringResource(id = R.string.bookshelf_info_title)) },
        onCloseClick = { onAction(BookshelfInfoSheetAction.Close) },
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(top = it.calculateTopPadding())
        ) {
            val scrollState = rememberScrollState()
            if (LocalNavigationState.current !is NavigationState.NavigationBar && scrollState.canScrollBackward) {
                HorizontalDivider()
            }
            Column(
                Modifier
                    .weight(1f)
                    .drawVerticalScrollbar(scrollState)
                    .verticalScroll(scrollState)
            ) {
                BookshelfThumbnails(
                    lazyPagingItems = lazyPagingItems,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
                ActionChips(
                    isProgressScan = uiState.isProgressScan,
                    enabled = uiState.enabled,
                    onScanClick = { onAction(BookshelfInfoSheetAction.Scan) },
                    onReThumbnailsClick = { onAction(BookshelfInfoSheetAction.ThumbnailRegeneration) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
                BookshelfInfo(
                    bookshelf = uiState.bookshelf,
                    folder = uiState.folder
                )
            }
            if (scrollState.canScrollForward) {
                HorizontalDivider()
            }
            BookshelfActions(
                enabled = uiState.enabled,
                onRemoveClick = { onAction(BookshelfInfoSheetAction.Remove) },
                onEditClick = { onAction(BookshelfInfoSheetAction.Edit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = ComicTheme.dimension.margin,
                        vertical = ComicTheme.dimension.targetSpacing
                    )
            )
        }
    }
}

@Composable
private fun BookshelfThumbnails(
    lazyPagingItems: LazyPagingItems<BookThumbnail>,
    modifier: Modifier = Modifier,
    carouselState: CarouselState = rememberCarouselState(itemCount = lazyPagingItems::itemCount),
) {
    HorizontalUncontainedCarousel(
        state = carouselState,
        itemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 8.dp),
        modifier = modifier
//            .height(136.dp)
    ) { index ->
        if (lazyPagingItems.itemCount <= 0) return@HorizontalUncontainedCarousel
        lazyPagingItems[index]?.let {
            SubcomposeAsyncImage(
                model = it,
                contentDescription = stringResource(id = R.string.bookshelf_desc_thumbnail),
                contentScale = ContentScale.Crop,
                error = {
                    if (LocalInspectionMode.current) {
                        Image(painter = previewPainter(), contentDescription = null)
                    } else {
                        Icon(
                            imageVector = ComicIcons.BrokenImage,
                            contentDescription = null,
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.Center)
                        )
                    }
                },
                loading = {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Center)
                    )
                },
                modifier = Modifier
                    .aspectRatio(1f)
                    .maskClip(MaterialTheme.shapes.medium)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActionChips(
    isProgressScan: Boolean,
    enabled: Boolean,
    onScanClick: () -> Unit,
    onReThumbnailsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.targetSpacing),
        modifier = modifier
    ) {
        AssistChip(
            onClick = onScanClick,
            enabled = enabled && !isProgressScan,
            label = { Text(text = stringResource(id = R.string.bookshelf_action_scan)) },
            leadingIcon = {
                if (isProgressScan) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                    )
                } else {
                    Icon(imageVector = ComicIcons.ShelvesSync, contentDescription = null)
                }
            }
        )
        AssistChip(
            onClick = onReThumbnailsClick,
            enabled = enabled,
            label = { Text(text = stringResource(id = R.string.bookshelf_action_scan_with_thumbnails)) },
            leadingIcon = { Icon(imageVector = ComicIcons.ImageSync, contentDescription = null) }
        )
        AssistChip(
            onClick = onReThumbnailsClick,
            enabled = enabled,
            label = { Text(text = stringResource(R.string.bookshelf_label_remove_thumbnail)) },
            leadingIcon = { Icon(imageVector = ComicIcons.ImageDelete, contentDescription = null) }
        )
    }
}

@Composable
private fun ColumnScope.BookshelfInfo(
    bookshelf: Bookshelf,
    folder: Folder,
) {
    val colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    ListItem(
        colors = colors,
        modifier = Modifier.height(56.dp),
        overlineContent = { Text(text = stringResource(R.string.bookshelf_label_bookshelf_type)) },
        headlineContent = { Text(text = stringResource(id = bookshelf.source())) },
    )
    ListItem(
        modifier = Modifier.height(56.dp),
        colors = colors,
        overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_display_name)) },
        headlineContent = { Text(text = bookshelf.displayName) },
    )

    when (bookshelf) {
        is InternalStorage -> {
            ListItem(
                colors = colors,
                overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_path)) },
                headlineContent = { Text(text = folder.path) },
            )
        }

        is SmbServer -> {
            ListItem(
                modifier = Modifier.height(56.dp),
                colors = colors,
                overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_host)) },
                headlineContent = { Text(text = bookshelf.host) },
            )
            ListItem(
                modifier = Modifier.height(56.dp),
                colors = colors,
                overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_port)) },
                headlineContent = { Text(text = bookshelf.port.toString()) },
            )
            ListItem(
                modifier = Modifier.height(56.dp),
                colors = colors,
                overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_path)) },
                headlineContent = { Text(text = folder.path) },
            )
            when (bookshelf.auth) {
                SmbServer.Auth.Guest -> {
                    ListItem(
                        modifier = Modifier.height(56.dp),
                        colors = colors,
                        overlineContent = { Text(text = stringResource(R.string.bookshelf_label_auth_method)) },
                        headlineContent = { Text(text = stringResource(R.string.bookshelf_label_guest)) },
                    )
                }

                is SmbServer.Auth.UsernamePassword -> {
                    ListItem(
                        modifier = Modifier.height(56.dp),
                        colors = colors,
                        overlineContent = { Text(text = stringResource(R.string.bookshelf_label_auth_method)) },
                        headlineContent = { Text(text = stringResource(R.string.bookshelf_label_Id_password)) },
                    )
                }
            }
        }

        ShareContents -> {
        }
    }
}

@Composable
private fun BookshelfActions(
    enabled: Boolean,
    onRemoveClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        TextButton(
            onClick = onRemoveClick,
            enabled = enabled,
            contentPadding = ButtonDefaults.TextButtonWithIconContentPadding
        ) {
            Icon(imageVector = ComicIcons.Delete, contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.bookshelf_action_delete))
        }
        FilledTonalButton(
            onClick = onEditClick,
            enabled = enabled,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        ) {
            Icon(imageVector = ComicIcons.Edit, contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.bookshelf_action_edit))
        }
    }
}

@Composable
@PreviewMultiScreen
private fun BookshelfInfoSheetPreview() {
    PreviewTheme2 {
        val lazyPagingItems =
            PagingData.flowData { BookThumbnail.from(fakeBookFile(it)) }.collectAsLazyPagingItems()

        val navigator = rememberSupportingPaneScaffoldNavigator<BookshelfFolder>(
            initialDestinationHistory = listOf(
                ThreePaneScaffoldDestinationItem(
                    SupportingPaneScaffoldRole.Extra,
                    BookshelfFolder(fakeSmbServer(), fakeFolder())
                )
            )
        )
        CanonicalScaffold(
            navigator = navigator,
            extraPane = { content ->
                BookshelfInfoSheet(
                    uiState = BookshelfInfoSheetUiState(
                        bookshelf = content.bookshelf,
                        folder = content.folder,
                        enabled = true,
                        isProgressScan = false
                    ),
                    lazyPagingItems = lazyPagingItems,
                    onAction = {},
                )
            },
        ) {
        }
    }
}
