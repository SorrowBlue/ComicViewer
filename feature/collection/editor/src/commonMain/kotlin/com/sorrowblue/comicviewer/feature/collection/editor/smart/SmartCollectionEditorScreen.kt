package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
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
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.CreateButton
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorForm
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialogContent
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import com.sorrowblue.comicviewer.framework.ui.material3.TextButtonWithIcon
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_cancel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Form

@Serializable
internal data class SmartCollectionEditorScreenUiState(
    val enabledForm: Boolean = true,
    val bookshelf: Map<BookshelfId?, String> = mapOf(),
)

@Composable
internal fun SmartCollectionEditorScreen(
    form: Form<SmartCollectionForm>,
    uiState: SmartCollectionEditorScreenUiState,
    title: @Composable () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isCompactWindowClass()) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = title,
                    navigationIcon = {
                        CloseIconButton(onClick = onCancel)
                    },
                    actions = {
                        TextButtonWithIcon(
                            onClick = form::handleSubmit,
                            icon = {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(ButtonDefaults.IconSize),
                                )
                            },
                            iconEnabled = !uiState.enabledForm,
                            enabled = uiState.enabledForm && form.meta.canSubmit,
                            modifier = Modifier.testTag("CreateButton"),
                        ) {
                            Text(text = "Save")
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
            SmartCollectionEditorForm(
                form = form,
                uiState = uiState,
                bookshelf = uiState.bookshelf,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ComicTheme.dimension.margin)
                    .padding(contentPadding),
            )
        }
    } else {
        val scrollState = rememberScrollState()
        BasicAlertDialog(
            onDismissRequest = onCancel,
            modifier = Modifier.padding(ComicTheme.dimension.margin),
        ) {
            AlertDialogContent(
                title = title,
                confirmButton = {
                    CreateButton(form = form)
                },
                dismissButton = {
                    TextButton(onClick = onCancel) {
                        Text(text = stringResource(Res.string.collection_editor_label_cancel))
                    }
                },
                scrollableState = scrollState,
            ) {
                SmartCollectionEditorForm(
                    form = form,
                    uiState = uiState,
                    bookshelf = uiState.bookshelf,
                )
            }
        }
    }
}
