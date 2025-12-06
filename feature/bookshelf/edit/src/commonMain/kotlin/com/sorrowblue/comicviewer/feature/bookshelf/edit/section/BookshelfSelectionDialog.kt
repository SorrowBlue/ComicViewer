package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialogContent
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_title_register
import org.jetbrains.compose.resources.stringResource

@Composable
fun BookshelfSelectionDialog(
    onBackClick: () -> Unit,
    onTypeClick: (BookshelfType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFullScreenDialog = isCompactWindowClass()
    val items = remember { BookshelfType.entries.toList() }
    val listState = rememberLazyListState()
    val title = remember {
        movableContentOf {
            Text(stringResource(Res.string.bookshelf_edit_title_register))
        }
    }
    val content = remember {
        movableContentOf { contentPadding: PaddingValues ->
            SelectionList(
                items = items,
                onSourceClick = { onTypeClick(it) },
                state = listState,
                contentPadding = contentPadding,
            )
        }
    }
    BasicAlertDialog(
        onDismissRequest = onBackClick,
        properties = DialogProperties(usePlatformDefaultWidth = !isFullScreenDialog),
        modifier = modifier,
    ) {
        if (isFullScreenDialog) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = title,
                        navigationIcon = { BackIconButton(onClick = onBackClick) },
                        windowInsets = WindowInsets.safeDrawing.only(AppBarWindowInsets),
                    )
                },
                contentWindowInsets = WindowInsets.safeDrawing.add(ContentWindowInsets),
                content = content,
            )
        } else {
            AlertDialogContent(
                title = title,
                content = {
                    content(PaddingValues())
                },
            )
        }
    }
}

private val ContentWindowInsets
    @Composable
    get() = ComicTheme.dimension.run {
        WindowInsets(left = margin, right = margin, bottom = margin)
    }

private val AppBarWindowInsets get() = WindowInsetsSides.Horizontal + WindowInsetsSides.Top
