package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.collection.editor.basic.section.BasicCollectionContent
import com.sorrowblue.comicviewer.feature.collection.editor.component.CollectionNameTextField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.CreateButton
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialogContent
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.material3.dialogPaddingHorizonal
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_cancel
import comicviewer.feature.collection.editor.generated.resources.collection_editor_title_basic_edit
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Form

@Serializable
internal data class BasicCollectionEditScreenUiState(val isLoading: Boolean = false)

@Composable
internal fun BasicCollectionEditScreen(
    uiState: BasicCollectionEditScreenUiState,
    form: Form<BasicCollectionForm>,
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit,
    onDeleteClick: (File) -> Unit,
) {
    val title = remember {
        movableContentOf {
            Text(stringResource(Res.string.collection_editor_title_basic_edit))
        }
    }
    if (isCompactWindowClass()) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = title,
                    navigationIcon = {
                        BackIconButton(
                            onClick = onBackClick,
                            enabled = !uiState.isLoading,
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = form::handleSubmit,
                            enabled = form.meta.canSubmit && !uiState.isLoading,
                        ) {
                            Icon(ComicIcons.Save, null)
                        }
                    },
                    windowInsets = WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
                    ),
                    scrollBehavior = scrollBehavior,
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { contentPadding ->
            BasicCollectionContent(
                lazyPagingItems = lazyPagingItems,
                header = {
                    CollectionNameTextField(
                        form = form,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = contentPadding.calculateTopPadding())
                            .padding(horizontal = ComicTheme.dimension.margin),
                    )
                },
                contentPadding = contentPadding.only(
                    PaddingValuesSides.Horizontal + PaddingValuesSides.Bottom,
                ),
                onDeleteClick = onDeleteClick,
            )
        }
    } else {
        BasicAlertDialog(
            onDismissRequest = onBackClick,
            modifier = Modifier.padding(ComicTheme.dimension.margin),
        ) {
            val lazyListState = rememberLazyListState()
            AlertDialogContent(
                title = title,
                confirmButton = {
                    CreateButton(form = form)
                },
                dismissButton = {
                    TextButton(onClick = onBackClick) {
                        Text(text = stringResource(Res.string.collection_editor_label_cancel))
                    }
                },
                scrollableState = lazyListState,
            ) {
                BasicCollectionContent(
                    lazyPagingItems = lazyPagingItems,
                    header = {
                        CollectionNameTextField(
                            form = form,
                            modifier = Modifier
                                .fillMaxWidth(),
                        )
                    },
                    contentPadding = AlertDialogDefaults.dialogPaddingHorizonal,
                    state = lazyListState,
                    onDeleteClick = onDeleteClick,
                )
            }
        }
    }
}
