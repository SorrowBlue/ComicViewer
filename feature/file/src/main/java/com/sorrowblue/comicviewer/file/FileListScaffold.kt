package com.sorrowblue.comicviewer.file

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.file.File
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
    modifier: Modifier = Modifier,
    state: ResponsiveScaffoldState<File> = rememberResponsiveScaffoldState(sideSheetState = rememberSideSheetFileState()),
    topBar: @Composable () -> Unit,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit,
) {
    ResponsiveScaffold(
        state = state,
        topBar = topBar,
        sideSheet = { file, contentPadding ->
            FileInfoSheet(
                file = file,
                contentPadding = contentPadding,
                onCloseClick = { state.sheetState.hide() },
                onReadLaterClick = { /*TODO*/ },
                onFavoriteClick = { /*TODO*/ },
                onOpenFolderClick = {/*TODO*/ }
            )
        },
        bottomSheet = {
            FileInfoBottomSheet(
                file = it,
                onReadLaterClick = { /*TODO*/ },
                onFavoriteClick = { /*TODO*/ },
                onOpenFolderClick = {/*TODO*/ },
                onDismissRequest = {}
            )
        },
        contentWindowInsets = contentWindowInsets,
        modifier = modifier,
        content = content
    )
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
