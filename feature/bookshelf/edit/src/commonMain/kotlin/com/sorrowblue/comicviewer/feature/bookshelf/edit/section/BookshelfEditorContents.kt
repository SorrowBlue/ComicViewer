package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenEvent
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfWizardPage
import com.sorrowblue.comicviewer.feature.bookshelf.edit.LocalEditScreenState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.rememberBookshelfEditScreenState
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_save
import org.jetbrains.compose.resources.stringResource

@Composable
context(context: BookshelfEditScreenContext)
internal fun BookshelfEditorContents(
    page: BookshelfWizardPage.Edit,
    contentPadding: PaddingValues,
    onComplete: () -> Unit,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberBookshelfEditScreenState(editType = page.editType)
    val scrollState = rememberScrollState()
    BookshelfEditorContents(
        onSaveClick = {
            state.form.handleSubmit()
        },
        scrollState = scrollState,
        contentPadding = contentPadding,
        modifier = modifier,
    ) {
        when (state) {
            is LocalEditScreenState -> DeviceEditorContents(
                form = state.form,
                folderSelectFieldState = state.folderSelectFieldState,
                uiState = state.uiState,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(
                        contentPadding.only(PaddingValuesSides.Top + PaddingValuesSides.Horizontal)
                            .plus(PaddingValues(bottom = ComicTheme.dimension.padding)),
                    ),
            )

            is SmbEditScreenState -> SmbEditorContents(
                form = state.form,
                uiState = state.uiState,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(
                        contentPadding.only(PaddingValuesSides.Top + PaddingValuesSides.Horizontal)
                            .plus(PaddingValues(bottom = ComicTheme.dimension.padding)),
                    ),
            )
        }
    }
    val currentOnChange by rememberUpdatedState(onChange)
    LaunchedEffect(state.formState.value) {
        currentOnChange(state.initialForm == state.formState.value)
    }
    EventEffect(state.events) {
        when (it) {
            BookshelfEditScreenEvent.Complete -> {
                onComplete()
            }
        }
    }
}

@Composable
internal fun BookshelfEditorContents(
    onSaveClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        content()
        AnimatedVisibility(scrollState.canScrollForward) {
            HorizontalDivider()
        }
        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .align(Alignment.End)
                .padding(
                    contentPadding.only(PaddingValuesSides.Bottom + PaddingValuesSides.Horizontal),
                )
                .padding(top = ComicTheme.dimension.padding)
                .testTag("SaveButton"),
        ) {
            Text(stringResource(Res.string.bookshelf_edit_label_save))
        }
    }
}
