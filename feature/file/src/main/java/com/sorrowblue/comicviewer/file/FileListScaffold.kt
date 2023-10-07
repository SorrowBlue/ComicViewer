package com.sorrowblue.comicviewer.file

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.MobilePreviews
import com.sorrowblue.comicviewer.framework.ui.fakeEmptyLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveScaffold
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveScaffoldState
import com.sorrowblue.comicviewer.framework.ui.responsive.SideSheetValueState
import com.sorrowblue.comicviewer.framework.ui.responsive.rememberResponsiveScaffoldState
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun rememberSideSheetFileState(
    initialValue: File? = null,
): SideSheetValueState<File> {
    return rememberSaveable(saver =
    Saver(
        save = { null },
        restore = { savedValue ->
            SideSheetValueState(savedValue)
        }
    )) {
        SideSheetValueState(initialValue)
    }
}

@Composable
fun FileListScaffold(
    state: ResponsiveScaffoldState<File> = rememberResponsiveScaffoldState(sideSheetState = rememberSideSheetFileState()),
    lazyPagingItems: LazyPagingItems<File>,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    onClickItem: (File) -> Unit,
    topBar: @Composable () -> Unit,
    uiState: FileContentUiState = FileContentUiState(),
    modifier: Modifier = Modifier,
) {
    ResponsiveScaffold(
        state = state,
        topBar = topBar,
        sideSheet = { file, contentPadding ->
            FileInfoSheet(file,
                contentPadding = contentPadding,
                onCloseClick = { state.sheetState.hide() }
            )
        },
        bottomSheet = {
            FileInfoBottomSheet(it)
        },
        contentWindowInsets = contentWindowInsets,
        modifier = modifier,
    ) {
        if (lazyPagingItems.isEmptyData) {
            if (LocalWindowSize.current.widthSizeClass == WindowWidthSizeClass.Compact) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        imageVector = ComicIcons.UndrawResumeFolder,
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )
                    Text(text = "「〇〇〇〇〇」は空です。", style = ComicTheme.typography.titleMedium)
                }
            } else {
                Surface(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                    shape = ComicTheme.shapes.large
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            imageVector = ComicIcons.UndrawResumeFolder,
                            contentDescription = null,
                            modifier = Modifier.size(200.dp)
                        )
                        Text(text = "「〇〇〇〇〇」は空です。", style = ComicTheme.typography.titleMedium)
                    }
                }
            }
        } else {
            FileContent(
                uiState = uiState,
                lazyPagingItems = lazyPagingItems,
                contentPadding = it,
                onClickItem = onClickItem,
                onLongClickItem = {
                    state.sheetState.show(it)
                }
            )
        }
    }
}

val Long.asFileSize: String
    get() {
        var a = this / 1024f
        return if (a < 1024) {
            "%.2f".format(a) + " KB"
        } else {
            a /= 1024f
            if (a < 1024) {
                "%.2f".format(a) + " MB"
            } else {
                a /= 1024f
                "%.2f".format(a) + " GB"
            }
        }
    }

val Long.asDateTime: String
    get() = Instant.ofEpochMilli(this)
        .atZone(ZoneOffset.systemDefault())
        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

fun lastReadPage(lastReadPage: Int, maxPage: Int) = "${lastReadPage}/${maxPage} pages"

@MobilePreviews
@Composable
fun PreviewFileListScreen() {
    PreviewTheme {
        FileListScaffold(
            state = rememberResponsiveScaffoldState(
                sideSheetState = rememberSideSheetFileState(
                )
            ),
            lazyPagingItems = fakeEmptyLazyPagingItems(),
            onClickItem = {},
            topBar = {},
        )
    }
}
