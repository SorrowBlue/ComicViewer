package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.contentDataType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntSize
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenForm.Auth
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.AuthField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DomainField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.HostField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PasswordField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PathField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PortField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.UsernameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.EditScreen
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.EditorDialog
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import soil.form.compose.rememberForm

internal data class SmbEditScreenUiState(
    val form: SmbEditScreenForm,
    override val editMode: BookshelfEditMode,
) : BookshelfEditScreenUiState

@Composable
internal fun SmbEditScreen(
    isDialog: Boolean,
    uiState: SmbEditScreenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onSubmit: (SmbEditScreenForm) -> Unit,
    scrollState: ScrollState = rememberScrollState(),
) {
    val form = rememberForm(
        initialValue = uiState.form,
        onSubmit = onSubmit,
        saver = kSerializableSaver<SmbEditScreenForm>()
    )

    val content = remember {
        movableContentWithReceiverOf<ColumnScope> {
            val dimension = ComicTheme.dimension
            DisplayNameField(
                form = form,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimension.targetSpacing)
                    .semantics { contentDataType = ContentDataType.None }
            )
            HostField(
                form = form,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimension.targetSpacing)
                    .semantics { contentDataType = ContentDataType.None }
            )
            PortField(
                form = form,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimension.targetSpacing)
                    .semantics { contentDataType = ContentDataType.None }
            )
            PathField(
                form = form,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimension.targetSpacing)
                    .semantics { contentDataType = ContentDataType.None }
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimension.targetSpacing * 2)
                    .semantics { contentDataType = ContentDataType.None }
            )
            AuthField(
                form = form,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimension.targetSpacing)
                    .semantics { contentDataType = ContentDataType.None }
            )
            AnimatedVisibility(
                visible = form.value.auth == Auth.UserPass,
                enter = fadeIn() + expandIn(initialSize = { IntSize(it.width, 0) }),
                exit = shrinkOut(targetSize = { IntSize(it.width, 0) }) + fadeOut(),
            ) {
                Column {
                    DomainField(
                        form = form,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing)
                            .semantics { contentDataType = ContentDataType.None }
                    )

                    UsernameField(
                        form = form,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing)
                            .semantics { contentType = ContentType.Username }
                    )

                    PasswordField(
                        form = form,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing)
                            .semantics { contentType = ContentType.Password }
                    )
                }
            }
        }
    }
    if (isDialog) {
        EditorDialog(
            form = form,
            uiState = uiState,
            onDismissRequest = onBackClick,
            scrollState = scrollState,
            content = content
        )
    } else {
        EditScreen(
            form = form,
            uiState = uiState,
            onBackClick = onBackClick,
            scrollState = scrollState,
            snackbarHostState = snackbarHostState,
            content = content
        )
    }
}
