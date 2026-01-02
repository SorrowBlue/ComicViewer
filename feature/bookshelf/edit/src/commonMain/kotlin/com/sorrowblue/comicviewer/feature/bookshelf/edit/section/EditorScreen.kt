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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditForm
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_save
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_title_edit
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Form

@Composable
internal fun EditorScreen(
    form: Form<out BookshelfEditForm>,
    uiState: BookshelfEditScreenUiState,
    onBackClick: () -> Unit,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.bookshelf_edit_title_edit)) },
                navigationIcon = {
                    CloseIconButton(onClick = onBackClick)
                },
                actions = {
                    TextButton(
                        onClick = form::handleSubmit,
                        enabled = !uiState.progress && form.meta.canSubmit,
                        modifier = Modifier.testTag("SaveButton"),
                    ) {
                        AnimatedContent(targetState = uiState.progress, label = "progress") {
                            if (it) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(ButtonDefaults.IconSize),
                                )
                            } else {
                                Text(text = stringResource(Res.string.bookshelf_edit_label_save))
                            }
                        }
                    }
                },
                windowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
                ),
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(contentPadding)
                .padding(horizontal = 24.dp),
        ) {
            content(this)
        }
    }
}
