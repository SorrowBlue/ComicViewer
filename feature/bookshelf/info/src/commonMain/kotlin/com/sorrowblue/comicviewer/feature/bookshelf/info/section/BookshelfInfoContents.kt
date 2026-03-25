package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.file.component.FileThumbnailsCarousel
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.ExtraPaneScaffoldDefaults
import com.sorrowblue.comicviewer.framework.ui.adaptive.isNavigationBar
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_internal_storage
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_smb
import org.jetbrains.compose.resources.stringResource

internal data class BookshelfInfoContentsUiState(
    val bookshelf: Bookshelf,
    val folder: Folder,
    val isScanningFile: Boolean = false,
    val isScanningThumbnail: Boolean = false,
)

@Composable
context(context: BookshelfInfoScreenContext)
internal fun BookshelfInfoContents(
    bookshelfFolder: BookshelfFolder,
    showNotificationPermissionRationale: (ScanType) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val state = rememberBookshelfInfoContentsState(bookshelfFolder)
    BookshelfInfoContents(
        uiState = state.uiState,
        lazyPagingItems = state.lazyPagingItems,
        onScanFileClick = state::onScanFileClick,
        onScanThumbnailClick = state::onScanThumbnailClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        contentPadding = contentPadding,
        modifier = modifier,
    )

    EventEffect(state.events) {
        when (it) {
            is BookshelfInfoContentsEvent.ShowNotificationPermissionRationale -> showNotificationPermissionRationale(
                it.type,
            )
        }
    }
}

@Composable
internal fun BookshelfInfoContents(
    uiState: BookshelfInfoContentsUiState,
    lazyPagingItems: LazyPagingItems<out FileThumbnail>,
    onScanFileClick: () -> Unit,
    onScanThumbnailClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val navigationSuiteType =
            NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())
        val color = if (navigationSuiteType.isNavigationBar) {
            ComicTheme.colorScheme.surfaceVariant
        } else {
            ComicTheme.colorScheme.surfaceContainerHigh
        }
        Box {
            FileThumbnailsCarousel(
                lazyPagingItems = lazyPagingItems,
                contentPadding = contentPadding
                    .only(PaddingValuesSides.Horizontal + PaddingValuesSides.Top)
                    .plus(
                        PaddingValues(
                            horizontal = ExtraPaneScaffoldDefaults.Padding,
                            vertical = ComicTheme.dimension.padding,
                        ),
                    ),
                modifier = Modifier.fillMaxWidth()
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                0f to color.copy(alpha = 1f),
                                0.8f to color.copy(alpha = 0.6f),
                                1f to color.copy(alpha = 0.0f),
                            ),
                            blendMode = BlendMode.DstIn,
                        )
                    },
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
                    .padding(contentPadding.only(PaddingValuesSides.Horizontal))
                    .padding(horizontal = ExtraPaneScaffoldDefaults.Padding)
                    .padding(bottom = 16.dp),
            ) {
                Text(
                    text = uiState.bookshelf.displayName,
                    style = ComicTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (uiState.bookshelf.type) {
                        BookshelfType.SMB -> {
                            Icon(ComicIcons.Dns, null)
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                text = stringResource(Res.string.bookshelf_info_label_smb),
                                style = ComicTheme.typography.labelLarge,
                            )
                        }

                        BookshelfType.DEVICE -> {
                            Icon(ComicIcons.SdStorage, null)
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                text = stringResource(
                                    Res.string.bookshelf_info_label_internal_storage,
                                ),
                                style = ComicTheme.typography.labelLarge,
                            )
                        }

                        null -> {}
                    }
                }
            }
        }
        when (uiState.bookshelf) {
            is DeviceStorage -> DeviceStorageCard(
                deviceStorage = uiState.bookshelf,
                folder = uiState.folder,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding.only(PaddingValuesSides.Horizontal))
                    .padding(horizontal = ExtraPaneScaffoldDefaults.Padding),
            )

            ShareContents -> Unit

            is SmbServer -> {
                SmbConnectionCard(
                    smbServer = uiState.bookshelf,
                    folder = uiState.folder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(contentPadding.only(PaddingValuesSides.Horizontal))
                        .padding(horizontal = ExtraPaneScaffoldDefaults.Padding),
                )
            }
        }
        Spacer(Modifier.size(ComicTheme.dimension.padding))
        BookshelfInfoButtons(
            isScanningFile = uiState.isScanningFile,
            isScanningThumbnail = uiState.isScanningThumbnail,
            onScanFileClick = onScanFileClick,
            onScanThumbnailClick = onScanThumbnailClick,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding.only(PaddingValuesSides.Horizontal))
                .padding(horizontal = ExtraPaneScaffoldDefaults.Padding),
        )
        Spacer(Modifier.size(ExtraPaneScaffoldDefaults.Padding))
    }
}
