package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenUiState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.component.CloseIconButton
import com.sorrowblue.comicviewer.framework.ui.marginPadding
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import soil.form.Submission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditScreen(
    uiState: BookshelfEditScreenUiState,
    onBackClick: () -> Unit,
    submission: Submission,
    scrollState: ScrollState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = uiState.editMode.title)) },
                navigationIcon = {
                    CloseIconButton(onClick = onBackClick)
                },
                actions = {
                    TextButton(
                        onClick = submission.onSubmit,
                        enabled = !submission.isSubmitting
                    ) {
                        AnimatedContent(
                            targetState = !submission.isSubmitting,
                            label = "progress"
                        ) {
                            if (it) {
                                Text(text = stringResource(id = R.string.bookshelf_edit_label_save))
                            } else {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                            }
                        }
                    }
                },
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .imePadding()
                .drawVerticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(contentPadding)
                .marginPadding(dimension = ComicTheme.dimension, horizontal = true, bottom = true)
        ) {
            content(this)
        }
    }
}
